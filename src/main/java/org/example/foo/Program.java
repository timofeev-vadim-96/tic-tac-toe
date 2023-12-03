package org.example.foo;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Program {
    private final char userDot = 'X';
    private final char aiDot = 'O';
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private final char[][] playGround;
    private final int sizeX;
    private final int sizeY;
    private final int dotCount;
    private final char empty_field = '*';

    public Program(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.dotCount = determineDotCount();
        playGround = new char[sizeY][sizeX];
        checkPlayGroundSize();
    }

    /**
     * Основная логика приложения
     */
    public void main() {
        do { //внешний цикл, отвечающий за новую игру
            initialization();
            while (true) { //цикл самой игры
                showPlayGround();
                Dot userChoice = userChoice();
                playGround[userChoice.getY()][userChoice.getX()] = userDot;
                if (isWin(userDot)) {
                    showPlayGround();
                    System.out.println("Слава пользователям!");
                    break;
                } else if (isDraw()) {
                    showPlayGround();
                    System.out.println("Ничья!");
                    break;
                }
                Dot aiChoice = aiChoice();
                playGround[aiChoice.getY()][aiChoice.getX()] = aiDot;
                if (isWin(aiDot)) {
                    showPlayGround();
                    System.out.println("Слава программам!");
                    break;
                } else if (isDraw()) {
                    System.out.println("Ничья!");
                    break;
                }
            }
            String answer = prompt("Желаете ли вы сыграть еще раз? Y = да");
            if (!answer.equalsIgnoreCase("Y")) break;
        } while (true);
    }

    /**
     * Инициализация игровой доски
     */
    private void initialization() {
        for (char[] ch : playGround) {
            Arrays.fill(ch, empty_field);
        }
    }

    /**
     * Вывод текущего состояния игрового поля
     */
    private void showPlayGround() {
        for (int i = 0; i <= sizeY; i++) {
            if (i == 0) System.out.print("+" + "\t");
            else System.out.print(i + "\t");
        }
        System.out.println();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j <= sizeY; j++) {
                System.out.print("----");
            }
            System.out.println();
            System.out.print(i + 1 + " | ");
            for (int j = 0; j < sizeY; j++) {
                System.out.print(playGround[i][j] + " | ");
            }
            System.out.println();
        }
        for (int j = 0; j <= sizeY; j++) {
            System.out.print("----");
        }
        System.out.println();
    }

    /**
     * Вывод указанного текста с приглашением к вводу чего-либо с клавиатуры
     */
    private String prompt(String text) {
        System.out.println(text);
        return scanner.nextLine();
    }

    /**
     * Логика хода пользователя
     */
    private Dot userChoice() {
        Dot dot;
        do {
            try {
                String choice = prompt("Введите координаты, где поставить 'X', через пробел: ");
                String[] arr = choice.split(" ");
                if (arr.length < 2) {
                    dot = new Dot(-1, 1); //для нового витка цикла
                    continue;
                }
                int x = Integer.parseInt(arr[0]) - 1;
                int y = Integer.parseInt(arr[1]) - 1;
                dot = new Dot(x, y);
                if (!isDotValid(dot)) System.out.println("Введенные координаты не валидны...");
                else if (isDotValid(dot) && !isCellEmpty(dot)) {
                    System.out.println("В указанных координатах поле занято!...");
                }
            } catch (NumberFormatException e) { //когда введенные данные не являются числами
                dot = new Dot(-1, 1); //для нового витка цикла
            }
        } while (!isDotValid(dot) || !isCellEmpty(dot));
        return dot;
    }

    /**
     * Логика хода компьютера
     */
    private Dot aiChoice() {
        Dot point;
        point = aiTryChooseDote(1);
        if (isDotValid(point)) return point;
        /*
         * для поля, размером более, чем 3х3 (кол-во комбинации фишек автоматически будет равна 4)
         */
        if (dotCount == 4) {
            point = aiTryChooseDote(2);
            if (isDotValid(point)) return point;
        }
        //просто ищем первую же точку игрока
        point = aiTryChooseDote(3);
        if (isDotValid(point)) return point;
        /*
         * если ни одна из предыдущих функций не нашла подходящую точку, в ход идет рандом
         */
        do {
            int x = random.nextInt(sizeX);
            int y = random.nextInt(sizeY);
            point = new Dot(x, y);
        } while (!isCellEmpty(point));
        return point;
    }

    /**
     * Метод будет пытаться найти комбинацию из dotCount - decrement фишек и выбрать рядом лежащую точку для хода
     *
     * @param decrement - декремент для dotCount (кол-ва фишек подряд на поле)
     * @return Если же такая комбинация найдена не будет, либо все точки не валидны - вернет не валидную точку
     */
    private Dot aiTryChooseDote(int decrement) {
        int innerDotCount = this.dotCount - decrement;
        Dot point;
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                point = new Dot(i, j);
                /*
                Верхние 4 условия отвечают за один из 4 методов определения победы.
                Внутри каждого условия по две попытки выбрать точку перед началом отрезка с фишками, либо в его конце
                 */
                if (checkWinHorizontal(point, userDot, innerDotCount)) {
                    //перед началом отрезка
                    if (isDotValid(new Dot(i - 1, j)) && isCellEmpty(new Dot(i - 1, j))) {
                        return new Dot(i - 1, j);
                    }
                    //в конец отрезка
                    else if (isDotValid(new Dot(i + innerDotCount, j)) && isCellEmpty(new Dot(i + innerDotCount, j))) {
                        return new Dot(i + innerDotCount, j);
                    }
                }
                if (checkWinVertical(point, userDot, innerDotCount)) {
                    //перед началом отрезка
                    if (isDotValid(new Dot(i, j - 1)) && isCellEmpty(new Dot(i, j - 1))) {
                        return new Dot(i, j - 1);
                    }
                    //в конец отрезка
                    else if (isDotValid(new Dot(i, j + innerDotCount)) &&
                            isCellEmpty(new Dot(i, j + innerDotCount))) {
                        return new Dot(i, j + innerDotCount);
                    }
                }
                if (checkWinDiagonalLeftUp(point, userDot, innerDotCount)) {
                    //перед началом отрезка
                    if (isDotValid(new Dot(i - 1, j + 1)) && isCellEmpty(new Dot(i - 1, j + 1))) {
                        return new Dot(i - 1, j + 1);
                    }
                    //в конец отрезка
                    if (isDotValid(new Dot(i + innerDotCount, j - innerDotCount)) &&
                            isCellEmpty(new Dot(i + innerDotCount, j - innerDotCount))) {
                        return new Dot(i + innerDotCount, j - innerDotCount);
                    }
                }
                if (checkWinDiagonalRightDown(point, userDot, innerDotCount)) {
                    //перед началом отрезка
                    if (isDotValid(new Dot(i - 1, j - 1)) && isCellEmpty(new Dot(i - 1, j - 1))) {
                        return new Dot(i - 1, j - 1);
                    }
                    //в конец отрезка
                    else if (isDotValid(new Dot(i + innerDotCount, j + innerDotCount)) &&
                            isCellEmpty(new Dot(i + innerDotCount, i + innerDotCount))) {
                        return new Dot(i + innerDotCount, j + innerDotCount);
                    }
                }
            }
        }
        return new Dot(-1, -1); //специально возвращаем невалидную точку в случае,
        // если не нашли нужную комбинацию фишек
    }

    /**
     * Проверка, является ли игровое поле пустым
     */
    private boolean isCellEmpty(Dot dot) {
        return playGround[dot.getY()][dot.getX()] == empty_field;
    }

    /**
     * Является ли указанная пользователем точка координат валидной
     */
    private boolean isDotValid(Dot dot) {
        return dot.getX() < sizeX && dot.getY() < sizeY && dot.isValid();
    }

    /**
     * Проверка на ничью
     */
    private boolean isDraw() {
        for (char[] ch : playGround) {
            for (char value : ch) {
                if (value != empty_field) return false;
            }
        }
        return true;
    }

    /**
     * Проверка на победу
     *
     * @param dot - пользователь или компьютер
     */
    private boolean isWin(char dot) {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                Dot point = new Dot(i, j);
                if (checkWinHorizontal(point, dot, this.dotCount) ||
                        checkWinVertical(point, dot, this.dotCount) ||
                        checkWinDiagonalLeftUp(point, dot, this.dotCount) ||
                        checkWinDiagonalRightDown(point, dot, this.dotCount)) return true;
            }
        }
        return false;
    }

    /**
     * Метод проверки условия победы по горизонтали
     *
     * @param point точка, от которой будем проверять количество фишек, необходимое для победы
     * @param dot   - фишка игрока
     */
    private boolean checkWinHorizontal(Dot point, char dot, int dotCount) {
        if (sizeX - dotCount >= point.getX()) { //проверка на возможность иметь справа от этой точки (включая ее)
            //необходимое кол-во фишек для победы
            int counter = 0;
            for (int k = 0; k < dotCount; k++) {
                if (playGround[point.getY()][point.getX() + k] == dot) counter++;
            }
            return counter == dotCount;
        }
        return false;
    }

    /**
     * Метод проверки условия победы по вертикали
     *
     * @param point точка, от которой будем проверять количество фишек, необходимое для победы
     * @param dot   - фишка игрока
     */
    private boolean checkWinVertical(Dot point, char dot, int dotCount) {
        if (sizeY - dotCount >= point.getY()) { //проверка на возможность иметь внизу от этой точки (включая ее)
            //необходимое кол-во фишек для победы
            int counter = 0;
            for (int k = 0; k < dotCount; k++) {
                if (playGround[point.getY() + k][point.getX()] == dot) counter++;
            }
            return counter == dotCount;
        }
        return false;
    }

    /**
     * Метод проверки условия победы по диагонали слева-вверх
     *
     * @param point точка, от которой будем проверять количество фишек, необходимое для победы
     * @param dot   - фишка игрока
     */
    private boolean checkWinDiagonalLeftUp(Dot point, char dot, int dotCount) {
        if (point.getY() - dotCount >= 0 && sizeX - dotCount >= point.getX()) { //проверка на возможность иметь
            // слева-вверх от этой точки (включая ее) необходимое кол-во фишек для победы
            int counter = 0;
            for (int k = 0; k < dotCount; k++) {
                if (playGround[point.getY() - k][point.getX() + k] == dot) counter++;
            }
            return counter == dotCount;
        }
        return false;
    }

    /**
     * Метод проверки условия победы по диагонали слева-вниз
     *
     * @param point точка, от которой будем проверять количество фишек, необходимое для победы
     * @param dot   - фишка игрока
     */
    private boolean checkWinDiagonalRightDown(Dot point, char dot, int dotCount) {
        if (sizeY - dotCount >= point.getY() && sizeX - dotCount >= point.getX()) { //проверка на возможность иметь
            // слева-вниз от этой точки (включая ее) необходимое кол-во фишек для победы
            int counter = 0;
            for (int k = 0; k < dotCount; k++) {
                if (playGround[point.getY() + k][point.getX() + k] == dot) counter++;
            }
            return counter == dotCount;
        }
        return false;
    }

    /**
     * Метод, определяющий количество фишек, необходимое для победы, в зависимости от размера доски
     */
    private int determineDotCount() {
        if (sizeX == 3 || sizeY == 3) return 3;
        else return 4;
    }

    private void checkPlayGroundSize() {
        if (sizeX < 3 || sizeY < 3) {
            throw new RuntimeException("Play ground is too small! It's cant be less, than 3x3!");
        }
    }
}

