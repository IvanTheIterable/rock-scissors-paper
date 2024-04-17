package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RockPaperScissorsServer {
    private static final int PORT = 12345;
    private static final ExecutorService pool = Executors.newFixedThreadPool(8);
    private static final List<Player> waitingPlayers = new ArrayList<>();
    private static final String[] elements = {"камень", "ножницы", "бумага"};
    private static final int[] beats = {1, 2, 0};

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Новый игрок присоединился!");
                pool.execute(new Greeting(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Greeting implements Runnable {
        private final Socket socket;
        private final Scanner input;
        private final PrintWriter output;

        private Greeting(Socket socket) throws IOException {
            this.socket = socket;
            this.input = new Scanner(socket.getInputStream());
            this.output = new PrintWriter(socket.getOutputStream(), true);
        }

        @Override
        public void run() {
            output.println("Добро пожаловать! Введите ваш ник:");
            String nickname = input.nextLine();

            Player player = new Player(socket, input, output, nickname);

            output.println("Ожидаем соперника...");
            synchronized (waitingPlayers) {
                if (!waitingPlayers.isEmpty()) {
                    output.println("Соперник найден...");
                    pool.submit(new GameSession(waitingPlayers.get(0), player));
                    waitingPlayers.remove(0);
                } else {
                    waitingPlayers.add(player);
                }
            }
        }
    }

    private record Player(Socket socket, Scanner input, PrintWriter output, String nickname) {
        public void closeSession() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void win() {
            output.println("Вы выиграли");
        }

        public void lose() {
            output.println("Вы проиграли");
        }
    }

    private record GameSession(Player player1, Player player2) implements Runnable {
        @Override
        public void run() {
            while (true) {
                sendMessage(String.format("Новая игра. %s против %s", player1.nickname, player2.nickname));
                sendMessage("Выберите: камень (0), ножницы (1) или бумага (2):");
                Future<Integer> choice1Future = pool.submit(new GetChoiceTask(player1.input, player1.output));
                Future<Integer> choice2Future = pool.submit(new GetChoiceTask(player2.input, player2.output));

                int choice1;
                int choice2;
                try {
                    choice1 = choice1Future.get();
                    choice2 = choice2Future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }

                sendMessage(String.format("%s выбрал %s, %s выбрал %s", player1.nickname, elements[choice1], player2.nickname, elements[choice2]));

                if (choice1 == choice2) {
                    sendMessage("Ничья");
                    continue;
                } else if (choice1 == beats[choice2]) {
                    player1.lose();
                    player2.win();
                } else {
                    player1.win();
                    player2.lose();
                }
                player1.closeSession();
                player2.closeSession();
                break;
            }
        }

        private void sendMessage(String message) {
            player1.output.println(message);
            player2.output.println(message);
        }

        private record GetChoiceTask(Scanner input, PrintWriter output) implements Callable<Integer> {
            @Override
            public Integer call() {
                int choice;
                while (true) {
                    try {
                        choice = Integer.parseInt(input.nextLine().toLowerCase());
                        if (choice >= 0 && choice <= 2) {
                            output.println("Вы выбрали " + elements[choice]);
                            output.println("Ждем выбор оппонента...");
                            break;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                    output.println("Неверный выбор. Попробуйте еще раз.");
                }
                return choice;
            }
        }
    }
}
