Нужно реализовать на Java мультиплеерный сервер для игры в “Камень, ножницы, бумага”

Игровой процесс

Игрок коннектится по telnet к серверу, вводит ник и ожидает начала игры. Сервер
подбирает ему оппонента. Когда оппонент найден, начинается бой.

Игроки вводят камень, ножницы или бумагу, сервер определяет выигравшего. Если ничья,
то раунд начинается заново. Если победитель есть, то сессия закрывается.

В процессе выполнения задания желательно не забывать о производительности и
качестве кода.

Инструкция:
1) Запустить RockPaperScissorsServer
2) telnet localhost 12345
