package MiniWindows.Insta.Vistas;

import MiniWindows.Insta.SesionInsta;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Excepciones.CuentaDesactivadaException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginInsta extends VBox {

    private ServicioInsta servicio;
    private Runnable onLoginExitoso;
    private Runnable onIrARegistro;

    public LoginInsta(ServicioInsta servicio, Runnable onLoginExitoso, Runnable onIrARegistro) {
        this.servicio = servicio;
        this.onLoginExitoso = onLoginExitoso;
        this.onIrARegistro = onIrARegistro;

        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #ff0055, #d62976, #962fbf);");
        VBox cardLogin = crearTarjetaLogin();
        VBox cardRegistroLink = crearTarjetaRegistroLink();

        VBox contenedor = new VBox(12, cardLogin, cardRegistroLink);
        contenedor.setMaxWidth(350);
        contenedor.setAlignment(Pos.CENTER);

        getChildren().add(contenedor);
    }

    private VBox crearTarjetaLogin() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(30, 25, 25, 25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dbdbdb; -fx-border-radius: 3; -fx-background-radius: 3;");

        Label lblTitulo = new Label("Instagram");
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        lblTitulo.setPadding(new Insets(0, 0, 15, 0));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Nombre de usuario");
        txtUser.setStyle("-fx-background-color: #fafafa; -fx-border-color: #dbdbdb; -fx-border-radius: 3; -fx-padding: 8;");

        // Campo de contraseña con opción de mostrar/ocultar
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Contraseña");
        txtPass.setStyle("-fx-background-color: #fafafa; -fx-border-color: #dbdbdb; -fx-border-radius: 3; -fx-padding: 8;");
        HBox.setHgrow(txtPass, Priority.ALWAYS);

        TextField txtPassVisible = new TextField();
        txtPassVisible.setPromptText("Contraseña");
        txtPassVisible.setStyle("-fx-background-color: #fafafa; -fx-border-color: #dbdbdb; -fx-border-radius: 3; -fx-padding: 8;");
        txtPassVisible.setManaged(false);
        txtPassVisible.setVisible(false);
        HBox.setHgrow(txtPassVisible, Priority.ALWAYS);

        Button btnVerPass = new Button("👁");
        btnVerPass.setStyle("-fx-background-color: #fafafa; -fx-border-color: #dbdbdb; -fx-cursor: hand;");

        btnVerPass.setOnAction(e -> {
            if (txtPass.isVisible()) {
                txtPassVisible.setText(txtPass.getText());
                txtPass.setVisible(false);
                txtPass.setManaged(false);
                txtPassVisible.setVisible(true);
                txtPassVisible.setManaged(true);
                btnVerPass.setText("🙈");
            } else {
                txtPass.setText(txtPassVisible.getText());
                txtPassVisible.setVisible(false);
                txtPassVisible.setManaged(false);
                txtPass.setVisible(true);
                txtPass.setManaged(true);
                btnVerPass.setText("👁");
            }
        });

        HBox passBox = new HBox(5, txtPass, txtPassVisible, btnVerPass);
        passBox.setAlignment(Pos.CENTER);

        Button btnIngresar = new Button("Iniciar sesión");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-cursor: hand;");

        Label lblError = new Label();
        lblError.setTextFill(Color.RED);
        lblError.setWrapText(true);

        btnIngresar.setOnAction(e -> {
            String user = txtUser.getText().trim();
            String pass = txtPass.isVisible() ? txtPass.getText().trim() : txtPassVisible.getText().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                lblError.setText("Complete todos los campos.");
                return;
            }

            try {
                UsuarioInsta u = servicio.autenticar(user, pass);
                if (u != null) {
                    SesionInsta.getInstancia().iniciarSesion(u);
                    if (onLoginExitoso != null) {
                        onLoginExitoso.run();
                    }
                } else {
                    lblError.setText("Credenciales incorrectas.");
                }
            } catch (CuentaDesactivadaException ex) {
                lblError.setText(ex.getMessage());
            }
        });

        box.getChildren().addAll(lblTitulo, txtUser, passBox, btnIngresar, lblError);
        return box;
    }

    private VBox crearTarjetaRegistroLink() {
        VBox box = new VBox(5);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dbdbdb; -fx-border-radius: 3; -fx-background-radius: 3;");

        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER);

        Label lblPregunta = new Label("¿No tienes una cuenta?");
        lblPregunta.setStyle("-fx-text-fill: #262626; -fx-font-size: 13px;");

        Hyperlink linkRegistro = new Hyperlink("Regístrate");
        linkRegistro.setStyle("-fx-text-fill: #0095f6; -fx-font-weight: bold; -fx-border-color: transparent; -fx-padding: 0;");
        linkRegistro.setOnAction(e -> {
            if (onIrARegistro != null) {
                onIrARegistro.run();
            }
        });

        hBox.getChildren().addAll(lblPregunta, linkRegistro);
        box.getChildren().add(hBox);
        return box;
    }
}