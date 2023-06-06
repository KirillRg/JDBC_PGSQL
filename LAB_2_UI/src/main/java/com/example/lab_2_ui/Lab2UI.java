package com.example.lab_2_ui;
import java.sql.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.util.Scanner;

public class Lab2UI extends Application {
    private static Scanner scanner = new Scanner(System.in);
    String table = "Students";
    String dbname = "lab_db";

    @Override
    public void start(Stage window) throws IOException {
        Label textComponent = new Label("Для регистрации необходимо выбрать режим Доступа");
        textComponent.setMaxSize(700,700);
        Button buttonAdmin = new Button("Войти как Админ");
        buttonAdmin.setMaxSize(300,100);
        Button buttonReader = new Button("Войти как Читатель");
        buttonReader.setMaxSize(300,100);

        HBox componentGroup = new HBox();
        componentGroup.getChildren().add(textComponent);
        componentGroup.setAlignment(Pos.CENTER);
        componentGroup.setSpacing(40);
        componentGroup.getChildren().addAll(buttonAdmin,buttonReader);
        Scene registration = new Scene(componentGroup, window.getWidth(), window.getHeight());
        componentGroup.setStyle("-fx-background-color: lightgreen;");

        window.setScene(registration);
        window.setFullScreen(true);
        window.show();

        buttonAdmin.setOnAction((event1) -> {
            Functions var1 = new Functions();
            Connection conn = var1.connect_to_DB(dbname, "postgres", "maximlox");
            UIDBFunctions(conn,dbname,table,window);
        });
        buttonReader.setOnAction((event2) -> {
            String reader_name = "reader";
            String reader_pass = "1234";
            try {
               Functions var2 = new Functions();
               Connection createUserConn = var2.connect_to_DB(dbname, "postgres", "maximlox");
               var2.newReaderFunction(createUserConn);
               var2.giveRightsFunction(createUserConn);
               CallableStatement cs = null;
               cs = createUserConn.prepareCall("SELECT newReader('" + reader_name + "', '" + reader_pass + "');");
               cs.executeQuery();
               var2.printTableFunction(createUserConn);
               var2.searchCityFunction(createUserConn, table);
               cs = createUserConn.prepareCall("SELECT giveRights('" + reader_name + "');");
               cs.executeQuery();
               cs.close();
               createUserConn.close();
           }catch (SQLException e){
               System.out.println(e);
           }
            Functions var3 = new Functions();
            Connection conn = var3.connect_to_DB(dbname, reader_name, reader_pass);
            UIDBFunctions(conn,dbname,table,window);
        });
    }

    public static void UIDBFunctions(Connection conn,String dbname,String table,Stage window){
        Functions var1 = new Functions();

        Button printTableBtn = new Button("Просмотр содержимого таблицы Студентов");
        Label printText = new Label();
        printText.setMinSize(400,100);
        printText.setText("Hello");
        //Основные кнопки
        Button button1 = new Button("Создание БД с таблицей Студентов");
        Button button2 = new Button("Добавить запись");
        Button button3 = new Button("Найти по городу");
        Button button4 = new Button("Обновление записи");
        Button button5 = new Button("Удаление записей по имени");
        Button button6 = new Button("Очистка таблицы");
        Button button7 = new Button("Удаление БД");
        button1.setWrapText(true);
        button2.setWrapText(true);
        button3.setWrapText(true);
        button4.setWrapText(true);
        button5.setWrapText(true);
        button6.setWrapText(true);
        button7.setWrapText(true);
        printTableBtn.setWrapText(true);

        //Добавление записи
        TextField insert_id = new TextField("Впишите id");
        TextField insert_name = new TextField("Впишите имя");
        TextField insert_city = new TextField("Впишите город");
        TextField insert_phone_number = new TextField("Впишите телефон");
        //Поиск по городу
        TextField search_city = new TextField("Впишите город");
        Label search_result = new Label("Результат будет здесь =)");
        //Обновление записи
        TextField update_id = new TextField("Впишите id");
        TextField update_name = new TextField("Обновить имя");
        TextField update_city = new TextField("Обновить город");
        TextField update_phone_number = new TextField("Обновить телефон");
        //Удалить по имени
        TextField delete_by_name = new TextField("Имя для удаления");


        BorderPane root = new BorderPane();
        VBox leftBox = new VBox();
        leftBox.setSpacing(10);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.getChildren().addAll(
                new HBox(button2,insert_id,insert_name,insert_city,insert_phone_number),
                new HBox(button4,update_id,update_name,update_city,update_phone_number),
                new HBox(button5,delete_by_name),
                new HBox(button3,search_city,search_result)
        );

        root.setLeft(leftBox);
        root.setRight(new HBox(printTableBtn, printText));
        root.setTop(new HBox(button1,button6,button7));

        Scene registrationAdmin = new Scene(root, window.getWidth(), window.getHeight());
        root.setStyle("-fx-background-color: lightgreen;");
        window.setScene(registrationAdmin);
        window.setFullScreen(true);

        printTableBtn.setOnAction((printEvent)->{
            try {
                var1.printTableFunction(conn);
                CallableStatement cs8 = null;
                cs8 = conn.prepareCall("Select printTable('" + table + "');");
                ResultSet res1 = cs8.executeQuery();
                String result ="";
                while (res1.next()) {
                    result=result + res1.getString(1) +"\n";
                }
                printText.setText(result);
                res1.close();
                cs8.close();
            }catch (SQLException e){
                System.out.println(e);
            }
        });
        button1.setOnAction((case1)->{
            try {
                Functions varToCreate = new Functions();
                Connection connToCreate = varToCreate.connect_to_DB("postgres", "postgres", "maximlox");
                Statement stat = connToCreate.createStatement();
                try {
                    stat.executeQuery("CREATE DATABASE " + dbname);
                } catch (SQLException e) {
                }
                ;
                System.out.println("|БД была создана|");
                stat.close();
                connToCreate.close();

                Connection createConn = var1.connect_to_DB(dbname, "postgres", "maximlox");
                var1.createTableFunction(createConn, table);//Создание таблицы
                CallableStatement cs1 = null;
                cs1 = createConn.prepareCall("SELECT createTable()");
                cs1.executeQuery();

                Stage notification = new Stage();
                Label label = new Label("Таблица создана");
                StackPane not = new StackPane(label);
                not.setAlignment(Pos.CENTER);
                Scene scene = new Scene(not, 200, 100);
                notification.setScene(scene);
                notification.show();
                System.out.println("|Таблица создана|");
                cs1.close();
                createConn.close();

            }catch (SQLException e){
                System.out.println(e);
            }
        });
        button2.setOnAction((case2)->{
            try {
                var1.insertValuesFunction(conn, table);//Заполнение таблицы значениями
                CallableStatement cs2 = null;

                int id = Integer.valueOf(insert_id.getText());//scanner.nextInt();
                String name = insert_name.getText();//scanner.next();
                String city = insert_city.getText();//scanner.next();
                String phone_num = insert_phone_number.getText();//scanner.next();
                cs2 = conn.prepareCall("Select insertValues(" + Integer.toString(id) + "," +
                        " '" + name + "', '" + city + "', '" + phone_num + "');");
                cs2.executeQuery();
                System.out.println("|Данные были добавлены|");
                cs2.close();
            }catch (SQLException e){
                System.out.println(e);
            }
        });
        button3.setOnAction((case3)->{
            try {
                var1.searchCityFunction(conn, table);
                CallableStatement cs3 = null;
                System.out.println("|Найти запись по Городу|\n" +
                        "Введите Город >>");
                String city = search_city.getText();//scanner.next();
                cs3 = conn.prepareCall("Select searchCity('" + city + "');");
                ResultSet res1 = cs3.executeQuery();
                System.out.println("|Результат поиска|");
                String result ="";
                while (res1.next()) {
                    result=result + res1.getString(1) +"\n";
                }
                search_result.setText(result);
                res1.close();
                cs3.close();
            }catch (SQLException e){
                System.out.println(e);
            }
        });
        button4.setOnAction((case4)->{
            try {
                var1.updateTableFunction(conn, table);
                CallableStatement cs4 = null;
                int id = Integer.valueOf(update_id.getText());//scanner.nextInt();
                String name = update_name.getText();//scanner.next();
                String city = update_city.getText();//scanner.next();
                String phone_num = update_phone_number.getText();//scanner.next();
                cs4 = conn.prepareCall("Select updateTable(" + Integer.toString(id) + "," +
                        " '" + name + "', '" + city + "', '" + phone_num + "');");
                cs4.executeQuery();
                cs4.close();
            }catch (SQLException e){
                System.out.println(e);
            }
        });
        button5.setOnAction((case5)->{
            try {
                var1.deleteByNameFunction(conn, table);
                CallableStatement cs5 = null;
                String name = delete_by_name.getText();//scanner.next();
                cs5 = conn.prepareCall("Select deleteByName('" + name + "');");
                cs5.executeQuery();
                cs5.close();
            }catch (SQLException e){
                System.out.println(e);
            }
        });
        button6.setOnAction((case6)->{
            try {
                var1.clearTableFunction(conn, table);
                CallableStatement cs6 = null;
                cs6 = conn.prepareCall("Select clearTable();");
                cs6.executeQuery();
                System.out.println("|Таблица была очищена|");
                cs6.close();
            }catch (SQLException e){
                System.out.println(e);
            }
        });
        button7.setOnAction((case7)->{
            try {
                conn.close();
                Functions varToDrop = new Functions();
                Connection connToDrop = varToDrop.connect_to_DB("postgres", "postgres", "maximlox");
                Statement stat = connToDrop.createStatement();
                try {
                    stat.executeQuery("DROP DATABASE IF EXISTS " + dbname);
                } catch (SQLException e) {
                }
                ;
                System.out.println("|БД была удалена|");
                stat.close();
                connToDrop.close();
            }catch (SQLException e){
                System.out.println(e);
            }
            window.close();
        });
    }
    public static void main(String[] args) {
        launch();
    }
}