package MiniWindows.Insta.Vistas;

import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Excepciones.UsernameDuplicadoException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import java.io.File;

public class RegistrarseInsta extends VBox {

    private ServicioInsta servicio;
    private Runnable onVolverALogin;
    private String rutaFotoSeleccionada = "";

    public RegistrarseInsta(ServicioInsta servicio, Runnable onVolverALogin) {
        this.servicio = servicio;
        this.onVolverALogin = onVolverALogin;

        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #ff0055, #d62976, #962fbf);");

        VBox cardForm = crearTarjetaRegistro();
        VBox cardVolver = crearTarjetaVolverLink();

        VBox contenedor = new VBox(15, cardForm, cardVolver);
        contenedor.setMaxWidth(350);
        contenedor.setAlignment(Pos.CENTER);

        getChildren().add(contenedor);
    }

    private VBox crearTarjetaRegistro() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dbdbdb; -fx-border-radius: 3; -fx-background-radius: 3;");

        Label lblTitulo = new Label("Instagram");
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));


        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre completo");

        ComboBox<Character> cbGenero = new ComboBox<>();
        cbGenero.getItems().addAll('M', 'F');
        cbGenero.setPromptText("Género");
        cbGenero.setMaxWidth(Double.MAX_VALUE);

        TextField txtUser = new TextField();
        txtUser.setPromptText("Nombre de usuario");

        // Campo de contraseña con toggle de visibilidad
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Contraseña");
        HBox.setHgrow(txtPass, Priority.ALWAYS);

        TextField txtPassVisible = new TextField();
        txtPassVisible.setPromptText("Contraseña");
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

        TextField txtEdad = new TextField();
        txtEdad.setPromptText("Edad");

        Button btnBuscarFoto = new Button("Seleccionar Foto de Perfil");
        btnBuscarFoto.setMaxWidth(Double.MAX_VALUE);

        Label lblFotoRuta = new Label("Ninguna foto seleccionada");
        lblFotoRuta.setStyle("-fx-font-size: 10px; -fx-text-fill: #737373;");

        btnBuscarFoto.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Foto de Perfil");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg")
            );
            File archivo = fileChooser.showOpenDialog(getScene().getWindow());
            if (archivo != null) {
                rutaFotoSeleccionada = archivo.getAbsolutePath();
                lblFotoRuta.setText(archivo.getName());
            }
        });

        Button btnRegistrar = new Button("Registrarte");
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);
        btnRegistrar.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-cursor: hand;");

        Label lblMsg = new Label();
        lblMsg.setWrapText(true);

        btnRegistrar.setOnAction(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                Character genero = cbGenero.getValue();
                String user = txtUser.getText().trim();
                String pass = txtPass.isVisible() ? txtPass.getText().trim() : txtPassVisible.getText().trim();
                String edadTexto = txtEdad.getText().trim();

                if (nombre.isEmpty() || genero == null || user.isEmpty() || pass.isEmpty() || edadTexto.isEmpty()) {
                    lblMsg.setTextFill(Color.RED);
                    lblMsg.setText("Llene los campos obligatorios.");
                    return;
                }

                String regexPassword = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

                if (!pass.matches(regexPassword)) {
                    lblMsg.setTextFill(Color.RED);
                    lblMsg.setText("La contraseña debe tener mínimo 8 caracteres, incluir letras, números y al menos un carácter especial.");
                    return;
                }

                int edad = Integer.parseInt(edadTexto);

                UsuarioInsta nuevo = new UsuarioInsta(nombre, genero, user, pass, edad, rutaFotoSeleccionada);
                boolean exito = servicio.registrarUsuario(nuevo);

                if (exito) {
                    lblMsg.setTextFill(Color.GREEN);
                    lblMsg.setText("¡Cuenta creada con éxito!");
                }
            } catch (NumberFormatException nfe) {
                lblMsg.setTextFill(Color.RED);
                lblMsg.setText("Edad inválida.");
            } catch (UsernameDuplicadoException ex) {
                lblMsg.setTextFill(Color.RED);
                lblMsg.setText(ex.getMessage());
            }
        });

        box.getChildren().addAll(lblTitulo, txtNombre, cbGenero, txtUser, passBox, txtEdad, btnBuscarFoto, lblFotoRuta, btnRegistrar, lblMsg);
        return box;
    }

    private VBox crearTarjetaVolverLink() {
        VBox box = new VBox(5);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dbdbdb; -fx-border-radius: 3; -fx-background-radius: 3;");

        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER);

        Label lblPregunta = new Label("¿Tienes una cuenta?");
        lblPregunta.setStyle("-fx-text-fill: #262626; -fx-font-size: 13px;");

        Hyperlink linkLogin = new Hyperlink("Entrar");
        linkLogin.setStyle("-fx-text-fill: #0095f6; -fx-font-weight: bold; -fx-border-color: transparent; -fx-padding: 0;");
        linkLogin.setOnAction(e -> {
            if (onVolverALogin != null) {
                onVolverALogin.run();
            }
        });

        hBox.getChildren().addAll(lblPregunta, linkLogin);
        box.getChildren().add(hBox);
        return box;
    }
}