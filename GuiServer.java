import java.util.HashMap;
import java.util.StringTokenizer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class GuiServer extends Application {

    TextField s1, s2, s3, s4, c1, c2;
    Button serverChoice, clientChoice, b1;
    HashMap<String, Scene> sceneMap;
    GridPane grid;
    HBox clientButtonBox;
    HBox buttonBox;
    VBox clientBox;
    Scene startScene;
    BorderPane startPane;
    Server serverConnection;
    Client clientConnection;
    int clientNum = 0;

    ListView<String> listItems, listItems2;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("The Networked Client/Server GUI Example");

        this.serverChoice = new Button("Server");
        this.serverChoice.setPrefSize(200, 100);
        this.serverChoice.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16;");

        this.serverChoice.setOnAction(e -> {
            primaryStage.setScene(sceneMap.get("server"));
            primaryStage.setTitle("This is the Server");
            serverConnection = new Server(data -> {
                Platform.runLater(() -> {
                    listItems.getItems().add(data.toString());
                });

            });

        });

        this.clientChoice = new Button("Client");
        this.clientChoice.setPrefSize(200, 100);
        this.clientChoice.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16;");
        this.clientChoice.setOnAction(e -> {
            primaryStage.setScene(sceneMap.get("client"));
            primaryStage.setTitle("This is a client");
            clientNum++;
            clientConnection = new Client(data -> {
                Platform.runLater(() -> {
                    listItems2.getItems().add(data.toString());
                });
            });

            clientConnection.start();
        });

        this.buttonBox = new HBox(50, serverChoice, clientChoice);
        buttonBox.setAlignment(Pos.CENTER);
        startPane = new BorderPane();
        startPane.setPadding(new Insets(70));
        startPane.setCenter(buttonBox);
        startPane.setStyle("-fx-background-color: #EEEEEE; -fx-font-family: 'Arial';");

        startScene = new Scene(startPane, 800, 400);

        listItems = new ListView<String>();
        listItems2 = new ListView<String>();

        c1 = new TextField();
        c2 = new TextField();
        b1 = new Button("Send");
        b1.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        b1.setOnAction(e -> {
            newInfo sentInfo = new newInfo();
            sentInfo.message = c1.getText();
            StringTokenizer st = new StringTokenizer(c2.getText(), " ");
            while (st.hasMoreTokens()) {
                sentInfo.sentNums.add(Integer.parseInt(st.nextToken()));
            }

            clientConnection.send(sentInfo);
            sentInfo.sentNums.clear();
            c1.clear();
            c2.clear();
        });

        sceneMap = new HashMap<String, Scene>();

        sceneMap.put("server", createServerGui());
        sceneMap.put("client", createClientGui());

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.setScene(startScene);
        primaryStage.show();

    }

    public Scene createServerGui() {

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(30));
        pane.setStyle("-fx-background-color: #90CAF9; -fx-font-family: 'Arial';");

        listItems.setStyle("-fx-control-inner-background: #E3F2FD; -fx-font-family: 'Arial';");
        pane.setCenter(listItems);

        return new Scene(pane, 500, 400);

    }

    public Scene createClientGui() {
        clientButtonBox = new HBox(20, c1, c2);
        clientButtonBox.setAlignment(Pos.CENTER);

        c1.setStyle("-fx-background-color: #E3F2FD; -fx-font-family: 'Arial';");
        c2.setStyle("-fx-background-color: #E3F2FD; -fx-font-family: 'Arial';");

        b1.setStyle("-fx-background-color: #42A5F5; -fx-text-fill: white; -fx-font-family: 'Arial';");

        clientBox = new VBox(20, clientButtonBox, b1, listItems2);
        clientBox.setAlignment(Pos.CENTER);
        clientBox.setPadding(new Insets(30));
        clientBox.setStyle("-fx-background-color: #90CAF9; -fx-font-family: 'Arial';");

        listItems2.setStyle("-fx-control-inner-background: #E3F2FD; -fx-font-family: 'Arial';");

        return new Scene(clientBox, 400, 400);

    }


}
