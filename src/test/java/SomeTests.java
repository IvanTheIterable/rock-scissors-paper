import org.example.RockPaperScissorsServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SomeTests {

    private static final int PORT = 12345;
    private Thread serverThread;

    @Before
    public void setUp() {
        serverThread = new Thread(() -> {
            RockPaperScissorsServer.main(null);
        });
        serverThread.start();
    }

    @After
    public void tearDown() {
        serverThread.interrupt();
    }

    @Test
    public void happyPath() throws IOException {
        try (
                Socket player1Socket = new Socket("localhost", PORT);
                Socket player2Socket = new Socket("localhost", PORT);
                PrintWriter player1Output = new PrintWriter(player1Socket.getOutputStream(), true);
                BufferedReader player1Input = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
                PrintWriter player2Output = new PrintWriter(player2Socket.getOutputStream(), true);
                BufferedReader player2Input = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()))
        ) {
            assertEquals("Добро пожаловать! Введите ваш ник:", player1Input.readLine());
            player1Output.println("Игрок1");
            assertEquals("Ожидаем соперника...", player1Input.readLine());

            assertEquals("Добро пожаловать! Введите ваш ник:", player2Input.readLine());
            player2Output.println("Игрок2");
            assertEquals("Ожидаем соперника...", player2Input.readLine());

            assertEquals("Соперник найден...", player2Input.readLine());

            assertEquals("Новая игра. Игрок1 против Игрок2", player1Input.readLine());
            assertEquals("Новая игра. Игрок1 против Игрок2", player2Input.readLine());

            assertEquals("Выберите: камень (0), ножницы (1) или бумага (2):", player1Input.readLine());
            player1Output.println("1");
            assertEquals("Вы выбрали ножницы", player1Input.readLine());
            assertEquals("Ждем выбор оппонента...", player1Input.readLine());

            assertEquals("Выберите: камень (0), ножницы (1) или бумага (2):", player2Input.readLine());
            player2Output.println("2");
            assertEquals("Вы выбрали бумага", player2Input.readLine());
            assertEquals("Ждем выбор оппонента...", player2Input.readLine());

            assertEquals("Игрок1 выбрал ножницы, Игрок2 выбрал бумага", player1Input.readLine());
            assertEquals("Игрок1 выбрал ножницы, Игрок2 выбрал бумага", player2Input.readLine());

            assertEquals("Вы выиграли", player1Input.readLine());
            assertEquals("Вы проиграли", player2Input.readLine());

            assertNull(player1Input.readLine());
            assertNull(player2Input.readLine());
        }
    }

    @Test
    public void happyPathTie() throws IOException {
        try (
                Socket player1Socket = new Socket("localhost", PORT);
                Socket player2Socket = new Socket("localhost", PORT);
                PrintWriter player1Output = new PrintWriter(player1Socket.getOutputStream(), true);
                BufferedReader player1Input = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
                PrintWriter player2Output = new PrintWriter(player2Socket.getOutputStream(), true);
                BufferedReader player2Input = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));
        ) {
            assertEquals("Добро пожаловать! Введите ваш ник:", player1Input.readLine());
            player1Output.println("Игрок1");
            assertEquals("Ожидаем соперника...", player1Input.readLine());

            assertEquals("Добро пожаловать! Введите ваш ник:", player2Input.readLine());
            player2Output.println("Игрок2");
            assertEquals("Ожидаем соперника...", player2Input.readLine());

            assertEquals("Соперник найден...", player2Input.readLine());

            assertEquals("Новая игра. Игрок1 против Игрок2", player1Input.readLine());
            assertEquals("Новая игра. Игрок1 против Игрок2", player2Input.readLine());

            assertEquals("Выберите: камень (0), ножницы (1) или бумага (2):", player1Input.readLine());
            player1Output.println("1");
            assertEquals("Вы выбрали ножницы", player1Input.readLine());
            assertEquals("Ждем выбор оппонента...", player1Input.readLine());

            assertEquals("Выберите: камень (0), ножницы (1) или бумага (2):", player2Input.readLine());
            player2Output.println("1");
            assertEquals("Вы выбрали ножницы", player2Input.readLine());
            assertEquals("Ждем выбор оппонента...", player2Input.readLine());

            assertEquals("Игрок1 выбрал ножницы, Игрок2 выбрал ножницы", player1Input.readLine());
            assertEquals("Игрок1 выбрал ножницы, Игрок2 выбрал ножницы", player2Input.readLine());

            assertEquals("Ничья", player1Input.readLine());
            assertEquals("Ничья", player2Input.readLine());

            assertEquals("Новая игра. Игрок1 против Игрок2", player1Input.readLine());
            assertEquals("Новая игра. Игрок1 против Игрок2", player2Input.readLine());
        }
    }
}

