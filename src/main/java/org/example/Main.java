package org.example;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static final String URL = "jdbc:sqlserver://localhost:64942;databaseName=PicoPlaca;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "12345678";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Sistema de consulta Pico y Placa vehicular.");
        System.out.print("Por favor, ingrese la placa del vehículo (ABC-0123): ");
        String placa = scanner.nextLine().trim();
        System.out.print("Por favor, ingrese la hora actual (HH:mm): ");
        String hora = scanner.nextLine().trim();
        System.out.print("Por favor, ingrese la fecha actual (yyyy-MM-dd): ");
        String fecha = scanner.nextLine().trim();

        verificacion(placa, hora, fecha);
    }

    public static void verificacion(String placa, String hora, String fecha) {
        char terminacionPlaca = placa.charAt(placa.length() - 1);

        LocalDate fechaIngresada = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DayOfWeek diaSemana = fechaIngresada.getDayOfWeek();
        String dia = diaSemana.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

        LocalTime horaActual = LocalTime.parse(hora);

        String sqlConsultaRestriccion =
                "SELECT D.Dia, D.PlacasRestringidas, H.Inicio, H.Fin " +
                        "FROM DiasPicoPlaca D " +
                        "JOIN HorasPicoPlaca H ON H.Turno = (CASE " +
                        "WHEN ? BETWEEN '06:00' AND '09:30' THEN 'Mañana' " +
                        "WHEN ? BETWEEN '16:00' AND '20:00' THEN 'Tarde' END) " +
                        "WHERE D.Dia = ? AND D.PlacasRestringidas = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement pstmt = conn.prepareStatement(sqlConsultaRestriccion)) {
                pstmt.setString(1, hora);
                pstmt.setString(2, hora);
                pstmt.setString(3, dia);
                pstmt.setString(4, String.valueOf(terminacionPlaca));

                ResultSet rs = pstmt.executeQuery();

                String tienePicoPlaca = "No";

                if (rs.next()) {
                    tienePicoPlaca = "Sí";
                    String diaConsulta = rs.getString("Dia");
                    String horaInicio = rs.getString("Inicio");
                    String horaFin = rs.getString("Fin");

                    LocalTime inicio = LocalTime.parse(horaInicio);
                    LocalTime fin = LocalTime.parse(horaFin);

                    if (horaActual.isAfter(inicio) && horaActual.isBefore(fin)) {
                        System.out.println("El vehículo con la placa " + placa + " tiene restricción el día " + diaConsulta +
                                " en el horario de " + horaInicio + " a " + horaFin);
                    } else {
                        System.out.println("El vehículo con la placa " + placa + " no tiene restricción en el horario actual.");
                    }
                } else {
                    System.out.println("El vehículo con la placa " + placa + " no tiene restricción hoy.");
                }
                insertarConsulta(conn, placa, hora, dia, tienePicoPlaca);
            }
        } catch (SQLException e) {
            System.out.println("Error al procesar la consulta: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void insertarConsulta(Connection conn, String placa, String hora, String dia, String tienePicoPlaca) {
        String sqlInsertarConsulta = "INSERT INTO Consultas (Placa, Hora, Dia, TienePicoPlaca) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsertarConsulta)) {
            pstmt.setString(1, placa);
            pstmt.setString(2, hora);
            pstmt.setString(3, dia);
            pstmt.setString(4, tienePicoPlaca);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar la consulta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
