package ua.project.calculator.files.classes;

import ua.project.calculator.files.classes.objects.ExpressionInHistory;
import ua.project.calculator.files.libs.parsers.impls.DataFileParser;
import ua.project.calculator.files.libs.parsers.impls.VariableParser;
import ua.project.calculator.files.libs.ClipboardIO;
import ua.project.calculator.files.libs.CustomException;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.HashMap;

@SuppressWarnings("ALL")

/**
 * <h1><b>======= Calculator =======</b></h1>
 *
 * Данный класс создаёт и добавляет все графические элементы,
 * Но также, он общается с "движком" проекта, или же с CalculatorEngine'ом.
 *
 * @author Глущенко Павло
 * @version 1.8.2
 * @see Calculator#launchGraphics()
 * @see CalculatorEngine
 */
public class Calculator {

    // ========================= ПЕРЕМЕННЫЕ, МАССИВЫ, ОБЪЕКТЫ ДЛЯ НАСТРОЙКИ И РАБОТЫ =============================

    JPanel windowContent = new JPanel();           // Панель для контента окна (Панель для панелей :) )
    JFrame frame = new JFrame("Calculator");       // Фрейм (окно) для калькулятора
    JMenuBar menuBar = new JMenuBar();             // Рядок меню
    JMenu fileMenu = new JMenu("File");            // Меню "Файл"
    JMenu modeMenu = new JMenu("Mode");            // Меню "Режим"
    JMenu editMenu = new JMenu("Edit");            // Меню "Правка"
    JMenu variableMenu = new JMenu("Variables");   // Меню "Переменные"

    //                                  КОМПОНЕНТЫ МЕНЮ
    //                                     > Файл <
    JMenuItem exitItem = new JMenuItem("Exit");                    // "Выход"
    JMenuItem newCountingItem = new JMenuItem("New Counting");     // "Новый Подсчёт"
    JMenuItem countItem = new JMenuItem("Count");                  // "Подсчитать"
    JMenuItem viewHistoryItem = new JMenuItem("View History");     // "Просмотреть Историю"
    JMenuItem exitWOSaveItem = new JMenuItem("Exit Without Save"); // "Выйти без сохраниния"

    //                                     > Режим <
    //          Радио-кнопка (переключатель) для выбора уровня математики
    ButtonGroup modesGroup = new ButtonGroup();          // Это - сам переключатель
    //                                                      Какой же переключатель без кнопок (для выбора режима)?
    JRadioButtonMenuItem simpleMath = new JRadioButtonMenuItem("Simple Math");      // Кнопка-Режим "Простая Математика"
    JRadioButtonMenuItem normalMath = new JRadioButtonMenuItem("Normal Math");      // Кнопка-Режим "Нормальная Математика"
    JRadioButtonMenuItem highMath  = new JRadioButtonMenuItem("High Math");         // Кнопка-Режим "Высшая Математика"

    //                                     > Правка <
    JMenuItem copyItem = new JMenuItem("Copy");                       // "Копировать"
    JMenuItem pasteItem = new JMenuItem("Paste");                     // "Вставить"

    //                                     > Переменные <
    JMenuItem addItem = new JMenuItem("Add");                        // "Добавить Переменную"
    JMenuItem deleteItem = new JMenuItem("Delete");                  // "Удалить Переменную"
    JMenuItem changeItem = new JMenuItem("Change");                  // "Изменить Переменную"
    JMenuItem viewItem = new JMenuItem("View");                      // "Просмотреть переменные"
    JMenuItem cleanUpItem = new JMenuItem("Clean Up expression");    // "Почистить Выражение"

    public static final String MAIN_FONT_NAME = "Comic Sans MS";
    public static final Font TEXT_FIELD_FONT = new Font(MAIN_FONT_NAME, Font.PLAIN, 26);         // Шрифт Text Field'а
    public static final Font BUTTON_FONT = new Font(MAIN_FONT_NAME, Font.PLAIN, 22);             // Шрифт кнопок
    public static final Font UNICODE_FONT = new Font("Lucida Sans Unicode", Font.PLAIN, 28);     // Это шрифт нужен для Unicode символов
    public static final Font MENU_FONT = new Font(MAIN_FONT_NAME, Font.PLAIN, 18);               // Вот шрифт для названий меню
    public static final Font ITEM_FONT = new Font(MAIN_FONT_NAME, Font.PLAIN, 16);               // А вот этот - уже для компонентов

    //                                                      Цвета
    public static final Color NORMAL_TEXT_COLOR = new Color(255, 255, 255);    // Белый (Обычный цвет текста)
    public static final Color BLACK_COLOR = new Color(31, 31, 31);             // Чёрный (Для цифр и точки)
    public static final Color BROWN_COLOR = new Color(91, 44, 8);              // Коричневый (Для действий)
    public static final Color RED_COLOR = new Color(133, 11, 8);               // Красный (Для спец. кнопок)
    public static final Color ORANGE_COLOR = new Color(255, 79, 6);            // Оранжевый (Для равно)
    public static final Color LIGHT_BLUE_COLOR = new Color(34, 141, 208);      // Голубой (Для высшей математики)
    public static final Color VERY_LIGHT_GREEN = new Color(224, 252, 218);     // Зеленоватый (Для Text Field'а)

    JTextField displayField = new JTextField(20);  // Поле для вывода данных

    JButton buttons[] = new JButton[10];        // Массив кнопок с цифрами

    JButton buttonPoint = new JButton(".");      // Кнопка "."
    JButton buttonEqual = new JButton("=");      // Кнопка "="

    JPanel pl;                // Панель для кнопок с цифрами, "." и "="
    JPanel p2;                // Панель для кнопок с действиями ("+", "-", "/", "*")
    JPanel p3;                // Панель для спец. кнопок ("MR", "M+", "M-", "MC")
    JPanel p4;                // Панель для высшей математики

    JButton buttonPlus = new JButton("+");               // Кнопка "+"
    JButton buttonMinus = new JButton("-");              // Кнопка "-"
    JButton buttonDivide = new JButton("/");             // Кнопка "/"
    JButton buttonMultiply = new JButton("*");           // Кнопка "*"

    JButton buttonMR = new JButton("MR");                // Кнопка "MR"
    JButton buttonMP = new JButton("M+");                // Кнопка "M+"
    JButton buttonMM = new JButton("M-");                // Кнопка "M-"
    JButton buttonMC = new JButton("MC");                // Кнопка "MC"
    JButton buttonBS = new JButton("\u2190");            // Кнопка "Backspace"

    JButton buttonBracket1 = new JButton("(");           // Кнопка "("
    JButton buttonBracket2 = new JButton(")");           // Кнопка ")"
    JButton buttonNegativeNum = new JButton("-x");   // Кнопка негативного числа
    //                                                      Кнопки степеней
    JButton buttonPowerX2 = new JButton("x\u00B2");        // Квадрат
    JButton buttonPowerX3 = new JButton("x\u00B3");        // Куб
    JButton buttonPowerXY = new JButton("x\u207F");        // Степень n

    CalculatorEngine calcEngine;                                                  // Об этом - чуть попозже
    CalculatorMathModes calculatorMathModes = new CalculatorMathModes(this);      // И об этом тоже
    ClipboardIO io = new ClipboardIO();                                           // Это - класс для взаимодействия с буфером обмена
    VariableParser variableMath = new VariableParser();           // Система замены переменных в выражении на их значения
    DataFileParser dataFileParser = new DataFileParser();         // Обработчик загруженных файлов с информацией

    /** <h1><b> Метод настройки и запуска графики </b></h1>
     * <p> Надо вызвать после объявления класса. </p>
     *
     */
    public void launchGraphics() {
        //                  Создание, настройка и добавление компонентов в меню на рядке меню :) (Тофтология)
        //                                            (И их действия + действия с горячими клавишами)
        //                                      > Файл <
        fileMenu.add(newCountingItem);           // Кнопка "Новый Подсчёт"
        fileMenu.add(countItem);                 // Кнопка "Подсчитать"
        fileMenu.addSeparator();
        fileMenu.add(viewHistoryItem);           // Кнопка "Просмотреть Историю"
        fileMenu.addSeparator();
        fileMenu.add(exitItem);                  // Кнопка "Выход"
        fileMenu.add(exitWOSaveItem);            // Кнопка "Выйти без сохраниния"

        exitItem.addActionListener(e -> exit());      // При нажатии на кнопку "Выход",
        //                                                       Выходим с кодом "0"
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));    // Выход на горячую клавишу Escape

        newCountingItem.addActionListener(e -> calcEngine.newCounting(""));     // При нажатии на кнопку "Новый Подсчёт",
        //                                                                         Запускаем обнуление
        newCountingItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));    // Новый подсчёт на горячую клавишу Ctrl+N

        countItem.addActionListener(e -> calcEngine.count(displayField.getText() + " = "));    // При нажатии на кнопку "Подсчитать",
        //                                                                                        Считаем результат
        countItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));      // Подсчёт на горячую клавишу Ctrl+K

        viewHistoryItem.addActionListener(e -> JCalculatorDialogs.historyViewingDialog(this));    // При нажатии на кнопку "Просмотреть Историю",
        //                                                                                           Включаем диалог просмотра

        exitWOSaveItem.addActionListener(e -> {            // При нажатии на кнопку "Выйти без сохраниния",
            //                                                Запрашиваем что не надо сохранять

            // Диалог используется не обычный, поэтому надо создать панель с его содержимым
            JPanel dialogPanel = new JPanel(new GridLayout(4, 1));

            JLabel label = new JLabel("Select features you would like to not save on exit:");
            label.setFont(JCalculatorDialogs.DIALOG_MESSAGE_FONT);
            dialogPanel.add(label);

            dialogPanel.add(new JLabel(""));

            JCheckBox variablesCheck = new JCheckBox("Variables");
            variablesCheck.setFont(JCalculatorDialogs.DIALOG_MESSAGE_FONT);
            dialogPanel.add(variablesCheck);

            JCheckBox historyCheck = new JCheckBox("History");
            historyCheck.setFont(JCalculatorDialogs.DIALOG_MESSAGE_FONT);
            dialogPanel.add(historyCheck);

            // Показываем диалог. Ответы по сохранению считываются потом
            boolean thenExit = JOptionPane.showConfirmDialog(null, dialogPanel, "What you would like to not save?", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION;
            if (thenExit) return;      // Если выбрали "Отмена", выходим из метода

            // После того как всё запрошено - можно приступить к делу
            exit(!variablesCheck.isSelected(), !historyCheck.isSelected());

            System.exit(0);
        });
        exitWOSaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, InputEvent.CTRL_MASK));
        // Выход без сохранения на горячую клавишу Ctrl+Esc

        //                                      > Режим <

        //          Радио-кнопка (переключатель) для выбора уровня математики

        //                                 Настройка Item'ов
        simpleMath.setSelected(true);      // Первый режим выбран по-умолчанию
        modesGroup.add(simpleMath);        // Добавление лёгкого
        modeMenu.add(simpleMath);
        modesGroup.add(normalMath);        // Добавление нормального
        modeMenu.add(normalMath);
        modesGroup.add(highMath);          // Добавление сложного
        modeMenu.add(highMath);

        //          Вот про что я обещал рассказать,
        //          Это обработчик событий переключателя
        //          Но для его работы, надо прикрепить кнопки-режимы
        simpleMath.addActionListener(calculatorMathModes);         // Лёгкий
        normalMath.addActionListener(calculatorMathModes);         // Нормальный
        highMath.addActionListener(calculatorMathModes);           // Сложный

        //                                       > Правка <

        editMenu.add(copyItem);             // Кнопка "Копировать"
        editMenu.add(pasteItem);            // Кнопка "Вставить"

        copyItem.addActionListener(e -> io.setText(displayField.getText()));      // При нажатии на кнопку "Копировать",
        //                                                                           Копируем (загружаем) в буфер обмена то, что сейчас на экране
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));   // Копирование на горячую клавишу Ctrl+C

        pasteItem.addActionListener(e -> {
            try {
                calcEngine.newCounting(displayField.getText() + io.getText());
            } catch (IOException | UnsupportedFlavorException e1) {
                e1.printStackTrace();
                calcEngine.handleCustomException(new CustomException("I can't read text from clipboard, \nplease, copy something.", "Pasting Error"));
            }
        });                                      // При нажатии на кнопку "Вставить",
        //                                          Вставляем в Text Field текст из буфера обмена
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));   // Вставка на горячую клавишу Ctrl+V

        //                                       > Переменные <

        variableMenu.add(addItem);           // Кнопка "Добавить Переменную"
        variableMenu.add(deleteItem);        // Кнопка "Удалить Переменную"
        variableMenu.add(changeItem);        // Кнопка "Изменить Переменную"
        variableMenu.add(viewItem);          // Кнопка "Просмотреть переменные"
        variableMenu.add(cleanUpItem);       // Кнопка  "Почистить Выражение"

        addItem.addActionListener(e -> JCalculatorDialogs.addingDialog(this));

        deleteItem.addActionListener(e -> JCalculatorDialogs.deletingDialog(this));

        viewItem.addActionListener(e -> JCalculatorDialogs.viewingDialog(this));

        changeItem.addActionListener(e -> JCalculatorDialogs.changingDialog(this));

        cleanUpItem.addActionListener(e -> {
            try {
                displayField.setText(variableMath.processText(displayField.getText(), true));
                calcEngine.displayText = displayField.getText();
                calcEngine.needOnlyNumber = false;
            } catch (CustomException e1) {
                calcEngine.handleCustomException(e1);
                calcEngine.resultCounted = true;
                calcEngine.needOnlyNumber = true;
                fileMenu.requestFocus();
            }
        });

        //                       Добавление меню на рядок меню :) (Тофтология)
        menuBar.add(fileMenu);     // "Файл"
        menuBar.add(modeMenu);     // "Режим"
        menuBar.add(editMenu);     // "Правка"
        menuBar.add(variableMenu); // "Переменные"

        for (int buttonNum = 0; buttonNum < 10; buttonNum++) {   // Заполнение массива с цифровыми Кнопками
            buttons[buttonNum] = new JButton(""+buttonNum);
            buttons[buttonNum].setFont(BUTTON_FONT);              // Установка их шрифта
            buttons[buttonNum].setBackground(BLACK_COLOR);        // Ну, и цвета заливки
            buttons[buttonNum].setForeground(NORMAL_TEXT_COLOR);  // А также, цвета текста
        }

        BorderLayout bl = new BorderLayout();    // Layout для результата действия
        windowContent.setLayout(bl);
        displayField.setFont(TEXT_FIELD_FONT);           // Установка шрифта поля
        windowContent.add("North", displayField);      // Добавление Text Field'а

                                          // Установка шрифтов других кнопок
        buttonPoint.setFont(BUTTON_FONT);      // Кнопка "."
        buttonEqual.setFont(BUTTON_FONT);      // Кнопка "="

        buttonPlus.setFont(BUTTON_FONT);         // Кнопка "+"
        buttonMinus.setFont(BUTTON_FONT);        // Кнопка "-"
        buttonDivide.setFont(BUTTON_FONT);       // Кнопка "/"
        buttonMultiply.setFont(BUTTON_FONT);     // Кнопка "*"

        buttonMR.setFont(BUTTON_FONT);           // Кнопка "MR"
        buttonMP.setFont(BUTTON_FONT);           // Кнопка "M+"
        buttonMM.setFont(BUTTON_FONT);           // Кнопка "M-"
        buttonMC.setFont(BUTTON_FONT);           // Кнопка "MC"
        buttonBS.setFont(UNICODE_FONT);          // Кнопка "Backspace"

        buttonBracket1.setFont(BUTTON_FONT);     // Кнопка "("
        buttonBracket2.setFont(BUTTON_FONT);     // Кнопка ")"
        buttonNegativeNum.setFont(BUTTON_FONT);  // Кнопка негативного числа
        //                                         Кнопки степеней
        buttonPowerX2.setFont(BUTTON_FONT);      // Квадрат
        buttonPowerX3.setFont(BUTTON_FONT);      // Куб
        buttonPowerXY.setFont(BUTTON_FONT);      // Степень n

        //                            Установка шрифтов меню и их компонентов
        fileMenu.setFont(MENU_FONT);        // Меню "Файл"
        exitItem.setFont(ITEM_FONT);        // Кнопка "Выход"
        newCountingItem.setFont(ITEM_FONT); // Кнопка "Новый Подсчёт"
        countItem.setFont(ITEM_FONT);       // Кнопка "Подсчёт"
        viewHistoryItem.setFont(ITEM_FONT); // Кнопка "Просмотреть Историю"
        exitWOSaveItem.setFont(ITEM_FONT);  // Кнопка "Выйти без сохраниния"

        modeMenu.setFont(MENU_FONT);        // Меню "Режим"
        simpleMath.setFont(ITEM_FONT);      // Кнопка-Режим "Простая Математика"
        normalMath.setFont(ITEM_FONT);      // Кнопка-Режим "Нормальная Математика"
        highMath.setFont(ITEM_FONT);        // Кнопка-Режим "Высшая Математика"

        editMenu.setFont(MENU_FONT);        // Меню "Правка"
        copyItem.setFont(ITEM_FONT);        // Кнопка "Копировать"
        pasteItem.setFont(ITEM_FONT);       // Кнопка "Вставить"

        variableMenu.setFont(MENU_FONT);    // Меню "Переменные"
        addItem.setFont(ITEM_FONT);         // Кнопка "Добавить Переменную"
        deleteItem.setFont(ITEM_FONT);      // Кнопка "Удалить Переменную"
        changeItem.setFont(ITEM_FONT);      // Кнопка "Изменить Переменную"
        viewItem.setFont(ITEM_FONT);        // Кнопка "Просмотреть переменные"
        cleanUpItem.setFont(ITEM_FONT);     // Кнопка  "Почистить Выражение"

        //                                     Задние фоны других кнопок и Text Field'а
        buttonPoint.setBackground(BLACK_COLOR);             // Кнопка "."
        buttonEqual.setBackground(ORANGE_COLOR);            // Кнопка "="

        buttonPlus.setBackground(BROWN_COLOR);              // Кнопка "+"
        buttonMinus.setBackground(BROWN_COLOR);             // Кнопка "-"
        buttonDivide.setBackground(BROWN_COLOR);            // Кнопка "/"
        buttonMultiply.setBackground(BROWN_COLOR);          // Кнопка "*"

        buttonMR.setBackground(RED_COLOR);                  // Кнопка "MR"
        buttonMP.setBackground(RED_COLOR);                  // Кнопка "M+"
        buttonMM.setBackground(RED_COLOR);                  // Кнопка "M-"
        buttonMC.setBackground(RED_COLOR);                  // Кнопка "MC"
        buttonBS.setBackground(RED_COLOR);                  // Кнопка "Backspace"

        buttonBracket1.setBackground(LIGHT_BLUE_COLOR);      // Кнопка "("
        buttonBracket2.setBackground(LIGHT_BLUE_COLOR);      // Кнопка ")"
        buttonNegativeNum.setBackground(LIGHT_BLUE_COLOR);   // Кнопка негативного числа
        //                                                    Кнопки степеней
        buttonPowerX2.setBackground(LIGHT_BLUE_COLOR);       // Квадрат
        buttonPowerX3.setBackground(LIGHT_BLUE_COLOR);       // Куб
        buttonPowerXY.setBackground(LIGHT_BLUE_COLOR);       // Степень n

        displayField.setBackground(VERY_LIGHT_GREEN);        // Text Field

        //                                     Цвета текста других кнопок
        buttonPoint.setForeground(NORMAL_TEXT_COLOR);         // Кнопка "."
        buttonEqual.setForeground(NORMAL_TEXT_COLOR);         // Кнопка "="

        buttonPlus.setForeground(NORMAL_TEXT_COLOR);          // Кнопка "+"
        buttonMinus.setForeground(NORMAL_TEXT_COLOR);         // Кнопка "-"
        buttonDivide.setForeground(NORMAL_TEXT_COLOR);        // Кнопка "/"
        buttonMultiply.setForeground(NORMAL_TEXT_COLOR);      // Кнопка "*"

        buttonMR.setForeground(NORMAL_TEXT_COLOR);            // Кнопка "MR"
        buttonMP.setForeground(NORMAL_TEXT_COLOR);            // Кнопка "M+"
        buttonMM.setForeground(NORMAL_TEXT_COLOR);            // Кнопка "M-"
        buttonMC.setForeground(NORMAL_TEXT_COLOR);            // Кнопка "MC"
        buttonBS.setForeground(NORMAL_TEXT_COLOR);            // Кнопка "Backspace"

        buttonBracket1.setForeground(NORMAL_TEXT_COLOR);      // Кнопка "("
        buttonBracket2.setForeground(NORMAL_TEXT_COLOR);      // Кнопка ")"
        buttonNegativeNum.setForeground(NORMAL_TEXT_COLOR);   // Кнопка негативного числа
        //                                                     Кнопки степеней
        buttonPowerX2.setForeground(NORMAL_TEXT_COLOR);       // Квадрат
        buttonPowerX3.setForeground(NORMAL_TEXT_COLOR);       // Куб
        buttonPowerXY.setForeground(NORMAL_TEXT_COLOR);       // Степень n

        pl = new JPanel();                     // Панель для кнопок с цифрами, "." и "="
        GridLayout gl1 = new GridLayout(4, 3);  // Layout для них
        pl.setLayout(gl1);

                  // Добавление кнопок на Панель для них

        for (int buttonNum = 0; buttonNum < 10; buttonNum++) {    // Кнопки с цифрами
            pl.add(buttons[buttonNum]);
        }
        pl.add(buttonPoint);     // Кнопка "."
        pl.add(buttonEqual);     // Кнопка "="

        windowContent.add("Center", pl);    // Размещение Панели в Центер

        p2 = new JPanel();
        GridLayout gl2 = new GridLayout(4, 1);   // Layout для кнопок с действиями ("+", "-", "/", "*")

        p2.setLayout(gl2);
                                  // Добавление кнопок с действиями
        p2.add(buttonPlus);       // Кнопка "+"
        p2.add(buttonMinus);      // Кнопка "-"
        p2.add(buttonMultiply);   // Кнопка "/"
        p2.add(buttonDivide);     // Кнопка "*"

        windowContent.add("East", p2);    // Эту Панель надо разместить на Востоке, то есть справа

        p3 = new JPanel();
        GridLayout gl3 = new GridLayout(1, 4);      // Layout для спец. кнопок ("MR", "M+", "M-", "MC")
        p3.setLayout(gl3);
        p3.add(buttonMR);                        // Кнопка "MR"
        p3.add(buttonMP);                        // Кнопка "M+"
        p3.add(buttonMM);                        // Кнопка "M-"
        p3.add(buttonMC);                        // Кнопка "MC"
        p3.add(buttonBS);                        // Кнопка "Backspace"
        windowContent.add("South", p3);  // Установка на Юг, то есть вниз

        p4 = new JPanel();
        GridLayout gl4 = new GridLayout(3, 1);      // Layout для кнопок с высшей математекой
        p4.setLayout(gl4);
        p4.add(buttonBracket1);                     // Кнопка "("
        p4.add(buttonBracket2);                     // Кнопка ")"
        p4.add(buttonNegativeNum);                  // Кнопка негативного числа
        //                                             Кнопки степеней
        p4.add(buttonPowerX2);                      // Квадрат
        p4.add(buttonPowerX3);                      // Куб
        p4.add(buttonPowerXY);                      // Степень n
        windowContent.add("West", p4);              // Закрепление на Западе, то есть слева

        frame.setContentPane(windowContent);        // Добавление контента окна (все панели, Text Field)
        frame.setJMenuBar(menuBar);                 // Добавление рядка меню
        frame.pack();                               // "Запакиваем" окно - автоматически создавая нужный размер
        frame.setVisible(true);                     // Делаем окно видимым
        // Операция выхода:
        // Выход из программы, при клике на кнопку выхода, в рядке заголовка + сохранение данных
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        // Отцентрирование окна
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().width/ 2 );

        frame.setTitle("Calculator");               // Установка названия окна (это в рядке заголовка)
        frame.setResizable(false);                  // И теперь, окно нельзя увеличить или уменьшить

        calcEngine = new CalculatorEngine(this);    // "Движок" калькулятора, его надо добавить,
        //                                             Чтоб можно было обрабатывать нажатия кнопок

                            // Прикрепление всех кнопок к addActionListener'у движка
        for (int buttonNum = 0; buttonNum < 10; buttonNum++) {   // Цифры
            buttons[buttonNum].addActionListener(calcEngine);
        }

        buttonPoint.addActionListener(calcEngine);       // Кнопка "."
        buttonPlus.addActionListener(calcEngine);        // Кнопка "+"
        buttonMinus.addActionListener(calcEngine);       // Кнопка "-"
        buttonDivide.addActionListener(calcEngine);      // Кнопка "/"
        buttonMultiply.addActionListener(calcEngine);    // Кнопка "*"
        buttonEqual.addActionListener(calcEngine);       // Кнопка "="
        buttonMR.addActionListener(calcEngine);          // Кнопка "MR"
        buttonMP.addActionListener(calcEngine);          // Кнопка "M+"
        buttonMM.addActionListener(calcEngine);          // Кнопка "M-"
        buttonMC.addActionListener(calcEngine);          // Кнопка "MC"
        buttonBS.addActionListener(calcEngine);          // Кнопка "Backspace"
        buttonBracket1.addActionListener(calcEngine);    // Кнопка "("
        buttonBracket2.addActionListener(calcEngine);    // Кнопка ")"
        buttonNegativeNum.addActionListener(calcEngine); // Кнопка негативного числа
        //                                                  Кнопки степеней
        buttonPowerX2.addActionListener(calcEngine);     // Квадрат
        buttonPowerX3.addActionListener(calcEngine);     // Куб
        buttonPowerXY.addActionListener(calcEngine);     // Степень n

        displayField.addFocusListener(calcEngine);       // Focus Listener для Text Field'а
    }

    /** <h1><b>======= Метод для загрузки переменных =======</b></h1>
     * <p>Использует обычную конструкцию с буфером, для повышения скорости эфективности.
     * Вызывается при запуске программы. </p>
     *
     * @see Calculator#saveVariables()
     */
    public void loadVariables() {
        FileReader myFile = null;            // Класс чтения из файла/Класс с файлом (фактически - ссылка/путь к файлу)
        BufferedReader myBuffer = null;      // Буфер для чтения из файла

        try {
            myFile = new FileReader("knownVariables.txt");       // Открываем файл
            myBuffer = new BufferedReader(myFile);               // Открываем буфер

            /*
            Прохождение по всем строчкам:
            while (true) {
                String line = myBuffer.readLine();
                if (line == null) break;
                doSomeWork();
            }
            */
            while (true) {
                String line = myBuffer.readLine();       // Строка из текстового файла
                if (line == null) break;


                // Этот код получает имя и значение переменной из строчки, там эта информация записана так:
                // name="(имя_переменной)",value="(значение_переменной)"
                HashMap<String, String> varData = dataFileParser.parse(line);
                String varName = varData.get("name").substring(1, varData.get("name").length() - 1);
                String varValue = varData.get("value").substring(1, varData.get("value").length());

                // После завершения обработки, надо записать переменную
                variableMath.knownVariables.put(varName, varValue);
            }
        } catch (FileNotFoundException e1) {  // На случай исключения FileNotFoundException (файл не найден)
            JCalculatorDialogs.errorMessage(new CustomException("FileNotFound Error",
                    "I can't find file to load variables,\nmaybe it not exists or you moved it to another place."));
            e1.printStackTrace();

        } catch (IOException e1) {            // На случай исключения IOException (ошибка доступа)
            e1.printStackTrace();

        } finally {                  // Заканчиваем чтение из файла, сработает в любом случае
            try {
                if (myBuffer != null) myBuffer.close();     // Закрытие буфера
                if (myFile != null) myFile.close();         // Закрытие файла
            } catch (IOException e1) {    // На случай исключения IOException ил любого дочернего от IOException
                e1.printStackTrace();
            }
        }
    }

    /** <h1><b>======= Метод для сохранения переменных =======</b></h1>
     * <p>Использует обычную конструкцию с буфером, для повышения скорости эфективности.
     * Вызывается при выходе из программы. </p>
     *
     * @see Calculator#loadVariables()
     * @see Calculator#exit()
     */
    public void saveVariables() throws CustomException {
        FileWriter myFile = null;            // Класс записи в файл/Класс с файлом (фактически - ссылка/путь к файлу)
        BufferedWriter myBuffer = null;      // Буфер для записи в файл

        try {
            // Если файла не сущействует, надо вызвать исключение
            if (!new File("knownVariables.txt").exists()) throw new CustomException("FileNotFound Error",
                    "I can't find file to save known variables,\nmaybe it not exists or you moved it to another place.");
            myFile = new FileWriter("knownVariables.txt", false);         // Открываем файл
            myBuffer = new BufferedWriter(myFile);                        // Открываем буфер

            for (String data : variableMath.knownVariables.keySet()) {    // Проход по всем переменным
                // Запись по такому принципу: "name=(имя_переменной)",value="(значение переменной)"
                myBuffer.write("name=\"" + data + "\",value=\"" + variableMath.knownVariables.get(data) + "\"\n");
            }

        } catch (IOException e1) {            // На случай исключения IOException (ошибка доступа)
            e1.printStackTrace();

        } finally {                  // Заканчиваем запись в файл, сработает в любом случае
            try {
                if (myBuffer != null) {
                    myBuffer.flush();    // "Сливаем" буфер
                    myBuffer.close();    // Закрытие буфера
                }
                if (myFile != null) myFile.close();      // Закрытие файла
            } catch (IOException e1) {    // На случай исключения IOException ил любого дочернего от IOException
                e1.printStackTrace();
            }
        }
    }

    /** <h1><b>======= Метод для загрузки истории =======</b></h1>
     * <p>Использует обычную конструкцию с буфером, для повышения скорости эфективности.
     * Вызывается при запуске программы. </p>
     *
     * @see Calculator#saveHistory()
     */
    public void loadHistory() {
        FileReader myFile = null;            // Класс чтения из файла/Класс с файлом (фактически - ссылка/путь к файлу)
        BufferedReader myBuffer = null;      // Буфер для чтения из файла

        try {
            myFile = new FileReader("history.txt");              // Открываем файл
            myBuffer = new BufferedReader(myFile);               // Открываем буфер

            /*
            Прохождение по всем строчкам:
            while (true) {
                String line = myBuffer.readLine();
                if (line == null) break;
                doSomeWork();
            }
            */
            while (true) {
                String line = myBuffer.readLine();       // Строка из текстового файла
                if (line == null) break;

                // Этот код получает выражение, результат и дату из строчки, там эта информация записана так:
                // text="(выражение)",result="(результат)",date="(дата)"
                HashMap<String, String> parsed = dataFileParser.parse(line);

                String result = parsed.get("result").substring(1, parsed.get("result").length() - 1);
                String expression = parsed.get("text").substring(1, parsed.get("text").length() - 1);
                String time = parsed.get("date").substring(1, parsed.get("date").length());

                // После завершения работы, надо сохранить результаты в историю
                calcEngine.history.add(new ExpressionInHistory(result, expression, time));
            }
        } catch (FileNotFoundException e1) {  // На случай исключения FileNotFoundException (файл не найден)
            JCalculatorDialogs.errorMessage(new CustomException("FileNotFound Error",
                    "I can't find file to load history,\nmaybe it not exists or you moved it to another place."));
            e1.printStackTrace();

        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (myBuffer != null) myBuffer.close();
                if (myFile != null) myFile.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /** <h1><b>======= Метод для сохранения истории =======</b></h1>
     * <p>Использует обычную конструкцию с буфером, для повышения скорости эфективности.
     * Вызывается при выходе из программы. </p>
     *
     * @see Calculator#saveHistory()
     * @see Calculator#exit()
     */
    public void saveHistory() throws CustomException {
        FileWriter myFile = null;            // Класс записи в файл/Класс с файлом (фактически - ссылка/путь к файлу)
        BufferedWriter myBuffer = null;      // Буфер для записи в файл

        try {
            // Если файла не сущействует, надо вызвать исключение
            if (!new File("history.txt").exists()) throw new CustomException("FileNotFound Error",
                    "I can't find file to save history,\nmaybe it not exists or you moved it to another place.");

            myFile = new FileWriter("history.txt", false);                // Открываем файл
            myBuffer = new BufferedWriter(myFile);                        // Открываем буфер

            for (ExpressionInHistory item : calcEngine.history) {    // Проход по всем записям в истории
                // Запись по такому принципу: text="(выражение)",result="(результат)",date="(дата)"
                myBuffer.write("text=\"" + item.expression +  "\",result=\"" + item.result + "\",date=\"" + item.date + "\"\n");
            }

        } catch (IOException e1) {            // На случай исключения IOException (ошибка доступа)
            e1.printStackTrace();
        } finally {                           // Заканчиваем запись в файл, сработает в любом случае
            try {
                if (myBuffer != null) {
                    myBuffer.flush();    // "Сливаем" буфер
                    myBuffer.close();    // Закрытие буфера
                }
                if (myFile != null) myFile.close();      // Закрытие файла
            } catch (IOException e1) {    // На случай исключения IOException ил любого дочернего от IOException
                e1.printStackTrace();
            }
        }
    }

    /** <h1><b>======= Метод для правильного выхода =======</b></h1>
     * <p>При выходе из программы надо сохранить известные переменные,
     * а также, так как не возникло никаких ошибок - выходим с кодом выхода "0".
     * Если файла не сыществует - сообщает об ошибке, и спрашивает:
     * "Выйти без сохранения или создать этот файл?".</p>
     *
     * @see Calculator#saveVariables()
     * @see Calculator#saveHistory()
     */
    public void exit() {
        exit(true, true);
    }

    /** <h1><b>======= Метод для правильного выхода =======</b></h1>
     * <p>При выходе из программы надо сохранить известные переменные,
     * а также, так как не возникло никаких ошибок - выходим с кодом выхода "0".
     * Если файла не сыществует - сообщает об ошибке, и спрашивает:
     * "Выйти без сохранения или создать этот файл?".</p>
     *
     * @param saveVariables сохранять ли переменные? Используется при выходе без сохранения.
     * @param saveHistory сохранять ли историю? Используется при выходе без сохранения.
     *
     * @see Calculator#saveVariables()
     * @see Calculator#saveHistory()
     */
    public void exit(boolean saveVariables, boolean saveHistory) {
        if (saveVariables) {
            try {
                saveVariables();      // Сохранение переменных

            } catch (CustomException e1) {      // Если возникла ошибка во время записи,
                String fullMessage = e1.getMessage();                                                               // Получение текста исключения
                String nameOfException = fullMessage.substring(0, fullMessage.indexOf("#"));                        // Имя ошибки
                String exceptionDesc = fullMessage.substring(fullMessage.indexOf("#") + 1, fullMessage.length());   // Описание ошибки

                //             Настройка шрифтов диалогового окна
                UIManager.put("OptionPane.messageFont", JCalculatorDialogs.DIALOG_MESSAGE_FONT);
                UIManager.put("OptionPane.buttonFont", JCalculatorDialogs.DIALOG_MESSAGE_FONT);

                Object[] options = {"Exit without save", "Create this file"};        // Возможные варианты ответа
                // Создание диалога: заголовок = имя ошибки, текст = описание ошибки, иконка = ошибочная иконка, варианты = options,
                // выбраный вариант = options[1] ("Создать этот файл")
                int reply = JOptionPane.showOptionDialog(null, nameOfException, exceptionDesc, JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE, null, options, options[1]);
                //                               При первом варианте - игнорируем результат
                if (reply == 1) {             // При втором варианте - создание файла и выход

                    try {
                        new File("knownVariables.txt").createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (saveHistory) {
            try {
                saveHistory();      // Сохранение истории

            } catch (CustomException e1) {      // Если возникла ошибка во время записи,
                String fullMessage = e1.getMessage();                                                               // Получение текста исключения
                String nameOfException = fullMessage.substring(0, fullMessage.indexOf("#"));                        // Имя ошибки
                String exceptionDesc = fullMessage.substring(fullMessage.indexOf("#") + 1, fullMessage.length());   // Описание ошибки

                //             Настройка шрифтов диалогового окна
                UIManager.put("OptionPane.messageFont", JCalculatorDialogs.DIALOG_MESSAGE_FONT);
                UIManager.put("OptionPane.buttonFont", JCalculatorDialogs.DIALOG_MESSAGE_FONT);

                Object[] options = {"Exit without save", "Create this file"};        // Возможные варианты ответа
                // Создание диалога: заголовок = имя ошибки, текст = описание ошибки, иконка = ошибочная иконка, варианты = options,
                // выбраный вариант = options[1] ("Создать этот файл")
                int reply = JOptionPane.showOptionDialog(null, nameOfException, exceptionDesc, JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE, null, options, options[1]);
                //                               При первом варианте - игнорируем результат
                if (reply == 1) {             // При втором варианте - создание файла и выход

                    try {
                        new File("history.txt").createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        System.exit(0);         // И под конец - выход
    }

    public static void main(String[] args) {
        Calculator calc = new Calculator();            // Создание и запуск нового калькулятора

        calc.launchGraphics();
        calc.loadVariables();
        calc.loadHistory();

//        calc.variableMath.knownVariables.put("var()", "123");
    }
}