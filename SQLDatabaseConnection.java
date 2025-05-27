import java.sql.Statement;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLDatabaseConnection {
    // Connect to your database.
    // Replace server name, username, and password with your credentials
    public static void main(String[] args) {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=tubesMIBD;"
                + "integratedSecurity=true;"
                + "trustServerCertificate=true;";

        ResultSet resultSet = null;

        try (Connection connection = DriverManager.getConnection(connectionUrl);) {
            // Code here.
            Statement statement = connection.createStatement();
            new UiMibd(connection, statement);
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            System.out.println("connection fail");
            e.printStackTrace();
        }
    }
}

class UiMibd {
    static String[] logo = {
            "      _____                       ",
            "     |  __ \\                     ",
            "  ___| |__) |   _ ___ _   _ _ __  ",
            " / __|  _  / | | / __| | | | '_ \\ ",
            " \\__ \\ | \\ \\ |_| \\__ \\ |_| | | | | ",
            " |___/_|  \\_\\__,_|___/\\__,_|_| |_|"
    };

    private static final Scanner sc = new Scanner(System.in);
    private static String query = null;
    private Connection connection;
    private Statement statement;
    private String NIK;

    public UiMibd(Connection conn, Statement stat) {
        connection = conn;
        statement = stat;
        NIK = "";
        printLoginPage();
    }

    public void printLoginPage() {
        printAsciiBox(logo);

        System.out.println(
                "Selemat datang pada sRusun, silakan masukan NIK yang sudah didaftar untuk login");
        System.out.print("NIK: ");
        String nikInput = sc.next();
        System.out.println();
        query = "SELECT OTP FROM [User] WHERE NIK = '" + nikInput + "'";
        ResultSet resultSet = null;
        String otpValid = null;
        try {
            resultSet = statement.executeQuery(query);
            resultSet.toString();
            resultSet.next();
            otpValid = resultSet.getString("OTP");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(
                "Masukkan OTP yang sudah ditentukan");
        System.out.print("OTP: ");
        String otpInput = sc.next();
        System.out.println();

        if (otpInput.equals(otpValid)) {
            NIK = nikInput;
            if (checkAdmin())
                printHomePageAdmin();
            else
                printHomePageUser();
        } else {
            System.out.println("OTP yang dimasukkan salah, tolong masukkan data lagi");
            System.out.println("ketik apapun untuk kembali");
            sc.next();
            System.out.println();
            printLoginPage();
        }
    }

    public boolean checkAdmin() {
        query = "SELECT IdR FROM RoleUser WHERE NIK='" + NIK + "'";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            int result = -1;
            while (resultSet.next()) {
                result = resultSet.getInt(1);
                if (result == 3)
                    return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void printHomePageAdmin() {
        printAsciiBox(logo);
        System.out.println("Selamat datang pada homepage\n");
        System.out.println("Silahkan pilih menu dibawah ini:");
        System.out.println("1) Melihat laporan pemakaian air");
        System.out.println("2) Mematikan/Menyalakan perangkat IoT");
        System.out.println("3) Mendaftar entitas");
        System.out.println("4) Mendownload log aktivitas\n");
        System.out.print("Pilihan(masukkan angka): ");
        int pilihan = sc.nextInt();
        if (pilihan == 1) {
            printLaporanAirAdmin();
        } else if (pilihan == 2) {
            printAktivasiPerangkatAdmin();
        } else if (pilihan == 3) {
            printPendaftaran();
        } else if (pilihan == 4) {
            printLogDownload();
        }
    }

    public void printHomePageUser() {
        printAsciiBox(logo);
        System.out.println("Selamat datang pada homepage\n");
        System.out.println("Silahkan pilih menu dibawah ini:");
        System.out.println("1) Melihat laporan pemakaian air");
        System.out.println("2) Mematikan/Menyalakan perangkat IoT");
        System.out.println("3) Mendaftarkan perangkat IoT");
        System.out.print("Pilihan(masukkan angka): ");
        int pilihan = sc.nextInt();
        if (pilihan == 1) {
            printLaporanAirUser();
        } else if (pilihan == 2) {
            printAktivasiPerangkatUser();
        } else if (pilihan == 3) {
            printPendaftaranPerangkat();
        }
    }

    public void printLaporanAirAdmin() {
        printAsciiBox(logo);
        displaySarusun(NIK);
        System.out.println("Ketik '.' jika ingin kembali ke homepage");
        System.out.print("Pilihan(masukkan ID): ");
        String idInput = sc.next();
        System.out.println();

        if (idInput.equals(".") == false) {
            ResultSet resultSet = null;
            try {
                String sql = "SELECT PenggunaanAirH, PenggunaanAirB, PenggunaanAirT FROM Sarusun WHERE IdS =" + idInput;
                resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 hari terakhir%n",
                            resultSet.getInt(1));
                    System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 minggu terakhir%n",
                            resultSet.getInt(2));
                    System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 tahun terakhir%n",
                            resultSet.getInt(3));
                    System.out.println("Ketik apapun untuk kembali");
                    sc.next();
                    printHomePageAdmin();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            printHomePageAdmin();
    }

    public void printLogDownload() {
        printAsciiBox(logo);
        System.out.println("Ketik '.' jika ingin kembali ke homepage");
        System.out.println(
                "Masukkan NIK untuk melihat log user tersebut atau # untuk melihat log semua user");
        System.out.print("NIK: ");
        String nikInput = sc.next();
        if (nikInput.equals(".") == true) {
            printHomePageUser();
        }
        System.out.println();
        System.out.println("Masukkan range tanggal log yang ingin didownload");
        System.out.println("Masukkan dengan format YYYYMMDD (20030130, 20011230)");
        System.out.print("Awal: ");
        String awal = sc.next();
        System.out.print("Akhir: ");
        String akhir = sc.next();
        System.out.println();
        System.out.println("Log sudah didownload sebagai file txt pada path .../Downloads");
        System.out.println("Ketik apapun untuk kembali");
        sc.next();
        printHomePageAdmin();
    }

    public void printLaporanAirUser() {
        printAsciiBox(logo);
        displaySarusun(NIK);
        System.out.println("Ketik '.' jika ingin kembali ke homepage");
        System.out.print("Pilihan(masukkan ID): ");
        String idInput = sc.next();
        System.out.println();

        if (idInput.equals(".") == false) {
            ResultSet resultSet = null;
            try {
                String sql = "SELECT PenggunaanAirH, PenggunaanAirB, PenggunaanAirT FROM Sarusun WHERE IdS =" + idInput;
                resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 hari terakhir%n",
                            resultSet.getInt(1));
                    System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 minggu terakhir%n",
                            resultSet.getInt(2));
                    System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 tahun terakhir%n",
                            resultSet.getInt(3));
                    System.out.println("Ketik apapun untuk kembali");
                    sc.next();
                    printHomePageUser();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            printHomePageUser();
    }

    public void printAktivasiPerangkatAdmin() {
        while (true) {
            printAsciiBox(logo);
            displaySarusun(NIK);
            System.out.println("Ketik '.' jika ingin kembali ke homepage");
            System.out.print("Pilihan (masukkan ID Sarusun): ");
            String idInput = sc.next();
            if (idInput.equals(".")) {
                printHomePageAdmin();
                return;
            }
            System.out.println();

            try {
                String sql = "SELECT noSerial, statusAir FROM Perangkat WHERE IdS = '" + idInput + "'";
                ResultSet resultSet = statement.executeQuery(sql);

                System.out.println("Silahkan pilih perangkat di sarusun yang sudah dipilih:");
                System.out.println("No.Serial           Status");

                boolean found = false;
                while (resultSet.next()) {
                    String noSerial = resultSet.getString("noSerial");
                    boolean statusAir = resultSet.getBoolean("statusAir"); // Asumsikan boolean
                    String statusStr = statusAir ? "Menyala" : "Mati";
                    System.out.printf("%-20s%s\n", noSerial, statusStr);
                    found = true;
                }

                if (!found) {
                    System.out.println("(Belum ada perangkat yang terdaftar di Sarusun ini)");
                    System.out.println("Ketik apapun untuk kembali");
                    sc.next();
                    continue;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println();
            System.out.println("Ketik '.' untuk memilih sarusun yang lain");
            System.out.print("Pilihan (masukkan noSerial): ");
            String noSerialInput = sc.next();
            if (noSerialInput.equals(".")) {
                continue;
            }

            try {
                String sql = "SELECT statusAir FROM Perangkat WHERE noSerial = '" + noSerialInput + "'";
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    boolean status = resultSet.getBoolean("statusAir");
                    if (status == true) {
                        String sqlUpdate = "UPDATE Perangkat SET statusAir = 0 WHERE noSerial = '" + noSerialInput
                                + "'";
                        statement.executeUpdate(sqlUpdate);
                    } else {
                        String sqlUpdate = "UPDATE Perangkat SET statusAir = 1 WHERE noSerial = '" + noSerialInput
                                + "'";
                        statement.executeUpdate(sqlUpdate);
                    }
                    String statusStr = !status ? "menyala" : "mati";
                    System.out.println("Perangkat IoT " + noSerialInput + " sekarang " + statusStr);
                } else {
                    System.out.println("Perangkat tidak ditemukan.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Ketik apapun untuk kembali");
            sc.next();
            break;
        }

        printHomePageAdmin();
    }

    public void printAktivasiPerangkatUser() {
        while (true) {
            printAsciiBox(logo);
            displaySarusun(NIK);
            System.out.println("Ketik '.' jika ingin kembali ke homepage");
            System.out.print("Pilihan (masukkan ID Sarusun): ");
            String idInput = sc.next();
            if (idInput.equals(".")) {
                printHomePageAdmin();
                return;
            }
            System.out.println();

            try {
                String sql = "SELECT noSerial, statusAir FROM Perangkat WHERE IdS = '" + idInput + "'";
                ResultSet resultSet = statement.executeQuery(sql);

                System.out.println("Silahkan pilih perangkat di sarusun yang sudah dipilih:");
                System.out.println("No.Serial           Status");

                boolean found = false;
                while (resultSet.next()) {
                    String noSerial = resultSet.getString("noSerial");
                    boolean statusAir = resultSet.getBoolean("statusAir"); // Asumsikan boolean
                    String statusStr = statusAir ? "Menyala" : "Mati";
                    System.out.printf("%-20s%s\n", noSerial, statusStr);
                    found = true;
                }

                if (!found) {
                    System.out.println("(Belum ada perangkat yang terdaftar di Sarusun ini)");
                    System.out.println("Ketik apapun untuk kembali");
                    sc.next();
                    continue;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println();
            System.out.println("Ketik '.' untuk memilih sarusun yang lain");
            System.out.print("Pilihan (masukkan noSerial): ");
            String noSerialInput = sc.next();
            if (noSerialInput.equals(".")) {
                continue;
            }

            try {
                String sql = "SELECT statusAir FROM Perangkat WHERE noSerial = '" + noSerialInput + "'";
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    boolean status = resultSet.getBoolean("statusAir");
                    if (status == true) {
                        String sqlUpdate = "UPDATE Perangkat SET statusAir = 0 WHERE noSerial = '" + noSerialInput
                                + "'";
                        statement.executeUpdate(sqlUpdate);
                    } else {
                        String sqlUpdate = "UPDATE Perangkat SET statusAir = 1 WHERE noSerial = '" + noSerialInput
                                + "'";
                        statement.executeUpdate(sqlUpdate);
                    }
                    String statusStr = !status ? "menyala" : "mati";
                    System.out.println("Perangkat IoT " + noSerialInput + " sekarang " + statusStr);
                } else {
                    System.out.println("Perangkat tidak ditemukan.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Ketik apapun untuk kembali");
            sc.next();
            break;
        }
        printHomePageUser();
    }

    public void printPendaftaranPerangkat() {
        printAsciiBox(logo);
        displaySarusun(NIK);
        System.out.println("Ketik '.' jika ingin kembali ke homepage");
        System.out.println("Masukkan data perangkat");
        System.out.println("Nomor harus serial harus 16 digit dan tidak ada spasi atau karakter spesial");
        System.out.print("Nomor Serial : ");
        String noSerialInput = sc.next();
        if (noSerialInput.equals(".") == false) {
            System.out.print("Id Sarusun lokasi perangkat: ");
            String IdS = sc.next();
            String input = "'" + noSerialInput + "', " + 0 + ", " + IdS;
            String sql = "INSERT INTO Perangkat VALUES (" + input + ")";
            try {
                statement.executeUpdate(sql);
                System.out.println();
                System.out.println("Perangkat telah didaftar");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Ketik apapun untuk kembali kepada homepage");
            sc.next();
        }
        printHomePageUser();
    }

    public void printPendaftaran() {
        while (true) {
            printAsciiBox(logo);
            System.out.println("Silahkan pilih entitas apa yang akan didaftar");
            System.out.println("1) User");
            System.out.println("2) Sarusun");
            System.out.println("3) Tower");
            System.out.println("4) Perangkat\n");
            System.out.println("Ketik '.' untuk kembali ke homepage");
            System.out.print("Pilihan(masukkan angka): ");
            String input = sc.next();
            System.out.println();
            if (input.equals("1")) {
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan data user");
                System.out.print("NIK: ");
                String nikInput = sc.next();
                if (nikInput.equals("."))
                    continue;
                System.out.print("Nama: ");
                String namaInput = sc.next();
                System.out.print("alamatDomisili: ");
                String alamatInput = sc.next();
                System.out.print("Nomor telepon: ");
                String noTelpInput = sc.next();
                System.out.println();
                try {
                    String sql = "INSERT INTO [User] (NIK, nama, alamatDomisili, noTelp, OTP) VALUES ('"
                            + nikInput + "', '" + namaInput + "', '" + alamatInput + "', '"
                            + noTelpInput + "', '" + 123456 + "')";
                    ResultSet resultSet = statement.executeQuery(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("User telah didaftar");
            } else if (input.equals("2")) {
                displayTower();
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan data sarusun");
                System.out.print("Id Tower: ");
                String towerInput = sc.next();
                if (towerInput.equals("."))
                    continue;
                System.out.print("Lantai: ");
                String lantaiInput = sc.next();
                displayUser();
                System.out.print("NIK Pemilik: ");
                String pemilikInput = sc.next();
                System.out.println();
                try {
                    String sql = "INSERT INTO Sarusun (Lantai, penggunaanAirH, penggunaanAirB, penggunaanAirT, nikPemilik, IdT) VALUES ('"
                            + lantaiInput + "', '" + 0 + "', '" + 0 + 0 + "', '" + pemilikInput
                            + "', '" + towerInput + "')";
                    ResultSet resultSet = statement.executeQuery(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Sarusun telah didaftar");
            } else if (input.equals("3")) {
                displayTower();
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan data tower");
                System.out.print("Nama Tower: ");
                String towerInput = sc.next();
                if (towerInput.equals("."))
                    continue;
                displayUser();
                System.out.print("Nama Pengelola: ");
                String pengelolaInput = sc.next();
                System.out.println();
                try {
                    String sql = "INSERT INTO Tower (IdT, nama) VALUES ('"
                            + towerInput + "', '" + pengelolaInput + "')";
                    ResultSet resultSet = statement.executeQuery(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Tower telah didaftar");
            } else if (input.equals("4")) {
                displayTower();
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan data perangkat");
                System.out.print("Nomor Serial : ");
                String towerInput = sc.next();
                System.out.println();
                if (towerInput.equals("."))
                    continue;
                displaySarusun(NIK);
                System.out.print("Id Sarusun lokasi perangkat: ");
                String sarusunInput = sc.next();
                System.out.println();
                try {
                    String sql = "INSERT INTO Perangkat (noSerial, IdS) VALUES ('" + towerInput
                            + "', '" + sarusunInput + "')";
                    ResultSet resultSet = statement.executeQuery(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Perangkat telah didaftar");
            }
            System.out.println("Ketik apapun untuk kembali kepada homepage");
            sc.next();
            printHomePageAdmin();
        }
    }

    public ResultSet displaySarusun(String NIK) {
        ResultSet resultSet = null;
        try {
            String sql = "SELECT IdS, nama, Sarusun.Lantai FROM Sarusun JOIN Tower ON Sarusun.IdT = Tower.IdT"
                    + " LEFT JOIN (SELECT IdT FROM Pengelola WHERE nikPengelola = '" + NIK
                    + "') AS Kelola ON Kelola.IdT = Sarusun.IdT"
                    + " WHERE nikPemilik = '" + NIK + "' OR Sarusun.IdT = Kelola.IdT";
            resultSet = statement.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("Silahkan pilih sarusun yang dimiliki akses:");
        System.out.println("ID       TOWER/LANTAI\n");
        try {
            while (resultSet.next()) {
                String id = resultSet.getString("IdS");
                String nama = resultSet.getString("nama");
                String lantai = resultSet.getString("Lantai");
                // %-4d = left-justify in 4 spacess
                // then a space, then the tower/layanan combo
                System.out.printf("%-8s %s/%s%n", id, nama, lantai);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        return resultSet;
    }

    public void displayAllSarusun() {
        System.out.println();
        System.out.println("Sarusun yang terdaftar:");
        System.out.println("ID              TOWER/LANTAI\n");
        System.out.println("1               PPAG/2A");
        System.out.println("2               Gedung 10/2");
        System.out.println("3               PPAG/3");
        System.out.println("4               Gedung 10/1");
        System.out.println();
    }

    public void displayUser() {
        System.out.println();
        String sql = "SELECT NIK, nama FROM [User]";
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println("NIK                  Nama");
            while (resultSet.next()) {
                System.out.printf("%-20s %s%n", resultSet.getString(1), resultSet.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    public void displayTower() {
        System.out.println();
        System.out.println("Tower yang terdaftar:");
        String sql = "SELECT * FROM TOWER";
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println("ID       Nama\n");
            while (resultSet.next()) {

                int idTower = resultSet.getInt("idT");
                String nama = resultSet.getString("nama");

                System.out.printf("%-8s %s%n", idTower, nama);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    public void printAsciiBox(String[] text) {
        System.out.println("││-----------------------------------------------------------││");
        int maxLength = 0;
        for (int i = 0; i < text.length; i++) {
            if (maxLength < text[i].length())
                maxLength = text[i].length();
        }

        String top = "┌" + "─".repeat(maxLength + 2) + "┐";
        String bottom = "└" + "─".repeat(maxLength + 2) + "┘";

        System.out.println(top);
        for (String line : text) {
            System.out.printf("│ %-" + maxLength + "s │%n", line);
        }
        System.out.println(bottom);
        System.out.println();
    }
}