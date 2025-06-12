import java.sql.Statement;
import java.util.Scanner;
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
        query = "SELECT OTP FROM [User] WHERE NIK = '" + nikInput + "' AND deleted = 0";
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
        System.out.println("4) Mendelete entitas");
        System.out.println("5) Mendownload log aktivitas\n");
        System.out.print("Pilihan(masukkan angka): ");
        int pilihan = sc.nextInt();
        if (pilihan == 1) {
            printLaporanAirAdmin();
        } else if (pilihan == 2) {
            printAktivasiPerangkatAdmin();
        } else if (pilihan == 3) {
            printPendaftaran();
        } else if (pilihan == 4) {
            deleteEntitas();
        } else if (pilihan == 5) {
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
        while (true) {
            printAsciiBox(logo);
            displaySarusun(NIK);
            System.out.println("Ketik '.' jika ingin kembali ke homepage");
            System.out.print("Pilihan(masukkan ID): ");
            String idInput = sc.next();
            System.out.println();
            if (idInput.equals(".") == false) {
                while (true) {
                    try {
                        String sql = "SELECT * FROM Sarusun WHERE deleted = 0 AND IdS=" + idInput;
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next() == false) {
                            System.out.println(
                                    "Tidak ada sarusun dengan Id itu atau sarusun dengan Id itu sudah didelete");
                            System.out.print("Masukkan Id Sarusun yang lainnya:");
                            idInput = sc.next();
                        } else
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ResultSet resultSet = null;
                try {
                    // laporan hari ini
                    String tanggal = java.time.LocalDate.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                    String sql = "SELECT SUM(literAir) FROM PemakaianAir WHERE tanggal = '" + tanggal + "'AND IdS ="
                            + idInput;
                    resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 hari terakhir%n",
                                resultSet.getInt(1));
                    }

                    // laporan dari hari ini sampai 1 minggu sebelumnya
                    String tanggalSebelum = java.time.LocalDate.now().minusWeeks(1)
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                    sql = "SELECT SUM(literAir) FROM PemakaianAir WHERE tanggal <= '" + tanggal
                            + "' AND IdS =" + idInput + " AND tanggal >= '" + tanggalSebelum + "'";
                    resultSet = statement.executeQuery(sql);

                    while (resultSet.next()) {
                        System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 minggu terakhir%n",
                                resultSet.getInt(1));
                    }

                    // laporan dari hari ini sampai 1 bulan sebelumnya
                    tanggalSebelum = java.time.LocalDate.now().minusMonths(1)
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                    sql = "SELECT SUM(literAir) FROM PemakaianAir WHERE tanggal <= '" + tanggal
                            + "' AND IdS =" + idInput + " AND tanggal >= '" + tanggalSebelum + "'";
                    resultSet = statement.executeQuery(sql);

                    while (resultSet.next()) {
                        System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 Bulan terakhir%n",
                                resultSet.getInt(1));
                    }

                    // laporan dari hari ini sampai 1 tahun sebelumnya
                    tanggalSebelum = java.time.LocalDate.now().minusYears(1)
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                    sql = "SELECT SUM(literAir) FROM PemakaianAir WHERE tanggal <= '" + tanggal
                            + "' AND IdS =" + idInput + " AND tanggal >= '" + tanggalSebelum + "'";
                    resultSet = statement.executeQuery(sql);

                    while (resultSet.next()) {
                        System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 Tahun terakhir%n",
                                resultSet.getInt(1));
                    }

                    sql = "INSERT INTO logPengguna OUTPUT INSERTED.IdL DEFAULT VALUES";
                    resultSet = statement.executeQuery(sql);
                    if (resultSet.next()) {
                        int IdL = resultSet.getInt(1);
                        String values = IdL + ", '" + NIK + "', " + idInput + ", GETDATE()";
                        sql = "INSERT INTO logMonitorAir VALUES (" + values + ")";
                        statement.executeUpdate(sql);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Ketik apapun untuk kembali ke homepage");
                sc.next();
                break;
            } else
                break;
        }
        printHomePageAdmin();
    }

    public void printLogDownload() {
        printAsciiBox(logo);
        System.out.println("Ketik '.' jika ingin kembali ke homepage");
        System.out.println("Masukkan NIK untuk melihat log user tersebut atau '#' untuk semua user");
        System.out.print("NIK: ");
        String nikInput = sc.next();
        if (nikInput.equals(".")) { // kembali ke homepage
            printHomePageUser();
            return;
        }

        System.out.println();
        System.out.println("Masukkan range tanggal log yang ingin didownload");
        System.out.println("Masukkan dengan format YYYYMMDD (misal: 20240613)");
        System.out.print("Awal  : ");
        String awal = sc.next();
        System.out.print("Akhir : ");
        String akhir = sc.next();
        System.out.println();

        // Tentukan path file di folder Downloads
        String fileName = "logDownload_" + System.currentTimeMillis() + ".txt";
        String userHome = System.getProperty("user.home");
        // Windows: biasanya userHome + "\\Downloads\\".
        // Jika di Linux/Mac, ubah separator-nya sesuai OS.
        // Ini cara umum yang bekerja di Windows:
        String filePath = userHome + "\\Downloads\\" + fileName;

        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(filePath))) {
            // Header untuk aktivasiPerangkat
            bw.write("=== LOG AKTIVASI PERANGKAT ===");
            bw.newLine();

            // Bentuk query untuk aktivasiPerangkat
            String sqlAktiv = "SELECT IdL, NIK, IdS, waktu " +
                    "FROM aktivasiPerangkat " +
                    "WHERE waktu BETWEEN '" + awal + "' AND '" + akhir + "'";
            if (!nikInput.equals("#")) {
                sqlAktiv += " AND NIK = '" + nikInput + "'";
            }
            ResultSet rsAktiv = statement.executeQuery(sqlAktiv);
            while (rsAktiv.next()) {
                int idL = rsAktiv.getInt("IdL");
                String nik = rsAktiv.getString("NIK");
                int idS = rsAktiv.getInt("IdS");
                java.sql.Timestamp waktu = rsAktiv.getTimestamp("waktu");
                bw.write(String.format("IdL: %d, NIK: %s, IdS: %d, Waktu: %s", idL, nik, idS, waktu.toString()));
                bw.newLine();
            }
            rsAktiv.close();

            bw.newLine();
            // Header untuk logMonitorAir
            bw.write("=== LOG MONITOR AIR ===");
            bw.newLine();

            // Bentuk query untuk logMonitorAir
            String sqlMon = "SELECT IdL, NIK, IdS, waktu " +
                    "FROM logMonitorAir " +
                    "WHERE waktu BETWEEN '" + awal + "' AND '" + akhir + "'";
            if (!nikInput.equals("#")) {
                sqlMon += " AND NIK = '" + nikInput + "'";
            }
            ResultSet rsMonitor = statement.executeQuery(sqlMon);
            while (rsMonitor.next()) {
                int idL = rsMonitor.getInt("IdL");
                String nik = rsMonitor.getString("NIK");
                int idS = rsMonitor.getInt("IdS");
                java.sql.Timestamp waktu = rsMonitor.getTimestamp("waktu");
                bw.write(String.format("IdL: %d, NIK: %s, IdS: %d, Waktu: %s", idL, nik, idS, waktu.toString()));
                bw.newLine();
            }
            rsMonitor.close();

        } catch (java.io.IOException | SQLException e) {
            e.printStackTrace();
            System.out.println("Gagal menulis log ke file.");
            System.out.println("Ketik apapun untuk kembali");
            sc.next();
            printHomePageUser();
            return;
        }

        System.out.println("Log sudah didownload sebagai file txt pada path:");
        System.out.println(filePath);
        System.out.println("Ketik apapun untuk kembali");
        sc.next();
        printHomePageAdmin();
    }

    public void printLaporanAirUser() {
        while (true) {
            printAsciiBox(logo);
            displaySarusun(NIK);
            System.out.println("Ketik '.' jika ingin kembali ke homepage");
            System.out.print("Pilihan(masukkan ID): ");
            String idInput = sc.next();
            System.out.println();
            if (idInput.equals(".") == false) {
                while (true) {
                    try {
                        String sql = "SELECT * FROM Sarusun WHERE deleted = 0 AND IdS=" + idInput;
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next() == false) {
                            System.out.println(
                                    "Tidak ada sarusun dengan Id itu atau sarusun dengan Id itu sudah didelete");
                            System.out.print("Masukkan Id Sarusun yang lainnya:");
                            idInput = sc.next();
                        } else
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ResultSet resultSet = null;
                try {
                    // laporan hari ini
                    String tanggal = java.time.LocalDate.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                    String sql = "SELECT SUM(literAir) FROM PemakaianAir WHERE tanggal = '" + tanggal + "'AND IdS ="
                            + idInput;
                    resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 hari terakhir%n",
                                resultSet.getInt(1));
                    }

                    // laporan dari hari ini sampai 1 minggu sebelumnya
                    String tanggalSebelum = java.time.LocalDate.now().minusWeeks(1)
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                    sql = "SELECT SUM(literAir) FROM PemakaianAir WHERE tanggal <= '" + tanggal
                            + "' AND IdS =" + idInput + " AND tanggal >= '" + tanggalSebelum + "'";
                    resultSet = statement.executeQuery(sql);

                    while (resultSet.next()) {
                        System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 minggu terakhir%n",
                                resultSet.getInt(1));
                    }

                    // laporan dari hari ini sampai 1 bulan sebelumnya
                    tanggalSebelum = java.time.LocalDate.now().minusMonths(1)
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                    sql = "SELECT SUM(literAir) FROM PemakaianAir WHERE tanggal <= '" + tanggal
                            + "' AND IdS =" + idInput + " AND tanggal >= '" + tanggalSebelum + "'";
                    resultSet = statement.executeQuery(sql);

                    while (resultSet.next()) {
                        System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 Bulan terakhir%n",
                                resultSet.getInt(1));
                    }

                    // laporan dari hari ini sampai 1 tahun sebelumnya
                    tanggalSebelum = java.time.LocalDate.now().minusYears(1)
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                    sql = "SELECT SUM(literAir) FROM PemakaianAir WHERE tanggal <= '" + tanggal
                            + "' AND IdS =" + idInput + " AND tanggal >= '" + tanggalSebelum + "'";
                    resultSet = statement.executeQuery(sql);

                    while (resultSet.next()) {
                        System.out.printf("Sarusun ini menggunakan %d Liter air selama 1 Tahun terakhir%n",
                                resultSet.getInt(1));
                    }

                    sql = "INSERT INTO logPengguna OUTPUT INSERTED.IdL DEFAULT VALUES";
                    resultSet = statement.executeQuery(sql);
                    if (resultSet.next()) {
                        int IdL = resultSet.getInt(1);
                        String values = IdL + ", '" + NIK + "', " + idInput + ", GETDATE()";
                        sql = "INSERT INTO logMonitorAir VALUES (" + values + ")";
                        statement.executeUpdate(sql);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Ketik apapun untuk kembali");
                sc.next();
                break;
            } else
                break;
        }
        printHomePageUser();
    }

    public void printAktivasiPerangkatAdmin() {
        while (true) {
            printAsciiBox(logo);
            displaySarusun(NIK);
            System.out.println("Ketik '.' jika ingin kembali ke homepage");
            System.out.print("Pilihan (masukkan ID Sarusun): ");
            String idInput = sc.next();
            while (true) {
                if (idInput.equals(".")) {
                    printHomePageAdmin();
                    return;
                }
                try {
                    String sql = "SELECT * FROM Sarusun WHERE deleted = 0 AND IdS=" + idInput;
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet.next() == false) {
                        System.out.println("Tidak ada sarusun dengan Id itu atau sarusun dengan Id itu sudah didelete");
                        System.out.print("Masukkan Id Sarusun yang lainnya:");
                        idInput = sc.next();
                    } else
                        break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println();

            try {
                String sql = "SELECT noSerial, statusAir FROM Perangkat WHERE deleted = 0 AND IdS = '" + idInput + "'";
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
                String sql = "SELECT statusAir FROM Perangkat WHERE deleted = 0 AND noSerial = '" + noSerialInput + "'";
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

                sql = "INSERT INTO logPengguna OUTPUT INSERTED.IdL DEFAULT VALUES";
                resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    int IdL = resultSet.getInt(1);
                    String values = IdL + ", '" + NIK + "', " + idInput + ", GETDATE()";
                    sql = "INSERT INTO aktivasiPerangkat VALUES (" + values + ")";
                    statement.executeUpdate(sql);
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
                printHomePageUser();
                return;
            }
            while (true) {
                try {
                    String sql = "SELECT * FROM Sarusun WHERE deleted = 0 AND IdS=" + idInput;
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet.next() == false) {
                        System.out.println("Tidak ada sarusun dengan Id itu atau sarusun dengan Id itu sudah didelete");
                        System.out.print("Masukkan Id Sarusun yang lainnya:");
                        idInput = sc.next();
                    } else
                        break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println();

            try {
                String sql = "SELECT noSerial, statusAir FROM Perangkat WHERE deleted = 0 AND IdS = '" + idInput + "'";
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
                String sql = "SELECT statusAir FROM Perangkat WHERE deleted = 0 AND noSerial = '" + noSerialInput + "'";
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
                sql = "INSERT INTO logPengguna OUTPUT INSERTED.IdL DEFAULT VALUES";
                resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    int IdL = resultSet.getInt(1);
                    String values = IdL + ", '" + NIK + "', " + idInput + ", GETDATE()";
                    sql = "INSERT INTO aktivasiPerangkat VALUES (" + values + ")";
                    statement.executeUpdate(sql);
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
            while (true) {
                try {
                    String sql = "SELECT * FROM Sarusun WHERE deleted = 0 AND IdS=" + IdS;
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet.next() == false) {
                        System.out.println("Tidak ada sarusun dengan Id itu atau sarusun dengan Id itu sudah didelete");
                        System.out.print("Masukkan Id Sarusun yang lainnya:");
                        IdS = sc.next();
                    } else
                        break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String input = "'" + noSerialInput + "', " + 0 + ", " + IdS;
            String sql = "INSERT INTO Perangkat(noSerial, statusAir, IdS) VALUES (" + input + ")";
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
            System.out.println("4) Perangkat");
            System.out.println("5) Pengelola\n");
            System.out.println("Ketik '.' untuk kembali ke homepage");
            System.out.print("Pilihan(masukkan angka): ");
            String input = sc.next();
            // biar daftar tower bisa pake spasi di nama tower
            sc.nextLine();
            System.out.println();
            if (input.equals("."))
                break;
            if (input.equals("1")) {
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan data user");
                System.out.print("NIK: ");
                String nikInput = sc.next();
                if (nikInput.equals("."))
                    continue;
                while (true) {
                    try {
                        if (nikInput.equals(".")) {
                            printHomePageAdmin();
                            return;
                        }
                        String sql = "SELECT * FROM [User] WHERE NIK= '" + nikInput + "'";
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            int deleted = resultSet.getInt("deleted");
                            if (deleted == 0) {
                                System.out.println(
                                        "Sudah ada record dengan NIK itu");
                                System.out.print("Masukkan NIK yang lainnya:");
                                nikInput = sc.next();
                            } else if (deleted == 1) {
                                System.out.println(
                                        "Sudah ada record deleted dengan NIK itu, record akan diaktivasi lagi");
                                sql = "UPDATE [User] SET deleted = 0 WHERE NIK = '" + nikInput + "'";
                                statement.executeUpdate(sql);
                                System.out.println("Record berhasil diaktivasi");
                                System.out.println("Ketik apapun untuk kembali ke homepage");
                                sc.next();
                                printHomePageAdmin();
                                return;
                            }
                        } else
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
                    statement.executeUpdate(sql);
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
                while (true) {
                    try {
                        if (towerInput.equals(".")) {
                            printHomePageAdmin();
                            return;
                        }
                        String sql = "SELECT * FROM Tower WHERE deleted = 0 AND IdT=" + towerInput;
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next() == false) {
                            System.out.println("Tidak ada tower dengan Id itu atau tower dengan Id itu sudah didelete");
                            System.out.print("Masukkan Id Tower Lain:");
                            towerInput = sc.next();
                        } else
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.print("Lantai: ");
                String lantaiInput = sc.next();
                displayUser();
                System.out.print("NIK Pemilik: ");
                String pemilikInput = sc.next();
                while (true) {
                    try {
                        String sql = "SELECT * FROM [User] WHERE deleted = 0 AND NIK='" + pemilikInput + "'";
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next() == false) {
                            System.out.println(
                                    "Tidak ada User dengan NIK itu atau User dengan NIK itu sudah didelete");
                            System.out.print("Masukkan NIK yang lainnya:");
                            pemilikInput = sc.next();
                        } else
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
                try {
                    String sql = "INSERT INTO Sarusun (Lantai, nikPemilik, IdT) VALUES ('" + lantaiInput + "', '"
                            + pemilikInput + "', " + towerInput + ")";
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Sarusun telah didaftar");
            } else if (input.equals("3")) {
                displayTower();
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan data tower");
                System.out.print("Nama Tower: ");
                String namaTowerInput = sc.nextLine();
                if (namaTowerInput.equals("."))
                    continue;
                displayUser();
                System.out.print("NIK Pengelola: ");
                String pengelolaInput = sc.next();
                while (true) {
                    try {
                        String sql = "SELECT * FROM [User] WHERE deleted = 0 AND NIK='" + pengelolaInput + "'";
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next() == false) {
                            System.out.println(
                                    "Tidak ada User dengan NIK itu atau User dengan NIK itu sudah didelete");
                            System.out.print("Masukkan NIK yang lainnya:");
                            pengelolaInput = sc.next();
                        } else
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
                try {
                    String sql = "INSERT INTO Tower (nama) VALUES ('"
                            + namaTowerInput + "')";
                    statement.executeUpdate(sql);
                    sql = "INSERT INTO Pengelola VALUES((SELECT MAX(IdT) FROM Tower), '" + pengelolaInput + "')";
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Tower telah didaftar");
            } else if (input.equals("4")) {
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan data perangkat");
                System.out.print("Nomor Serial : ");
                String noSerial = sc.next();
                if (noSerial.equals("."))
                    continue;
                while (true) {
                    try {
                        if (noSerial.equals(".")) {
                            printHomePageAdmin();
                            return;
                        }
                        String sql = "SELECT * FROM Perangkat WHERE noSerial= '" + noSerial + "'";
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            int deleted = resultSet.getInt("deleted");
                            if (deleted == 0) {
                                System.out.println("Sudah ada record dengan Nomor Serial itu");
                                System.out.print("Masukkan Nomor Serial yang lainnya:");
                                noSerial = sc.next();
                            } else if (deleted == 1) {
                                System.out.println(
                                        "Sudah ada record deleted dengan Nomor Serial itu, record akan diaktivasi lagi");
                                sql = "UPDATE Perangkat SET deleted = 0 WHERE noSerial = '" + noSerial + "'";
                                statement.executeUpdate(sql);
                                System.out.println("Record berhasil diaktivasi");
                                System.out.println("Ketik apapun untuk kembali ke homepage");
                                sc.next();
                                printHomePageAdmin();
                                return;
                            }
                        } else
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
                displayAllSarusun();
                System.out.print("Id Sarusun lokasi perangkat: ");
                String sarusunInput = sc.next();
                while (true) {
                    try {
                        String sql = "SELECT * FROM Sarusun WHERE deleted = 0 AND IdS=" + sarusunInput;
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next() == false) {
                            System.out.println(
                                    "Tidak ada sarusun dengan Id itu atau sarusun dengan Id itu sudah didelete");
                            System.out.print("Masukkan Id Sarusun yang lainnya:");
                            sarusunInput = sc.next();
                        } else
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
                try {
                    String sql = "INSERT INTO Perangkat (noSerial, IdS) VALUES ('" + noSerial
                            + "', '" + sarusunInput + "')";
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Perangkat telah didaftar");
            } else if (input.equals("5")) {
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan data pengelola");
                displayTower();
                System.out.print("Id Tower: ");
                String idTower = sc.next();
                if (idTower.equals("."))
                    continue;
                while (true) {
                    try {
                        if (idTower.equals(".")) {
                            printHomePageAdmin();
                            return;
                        }
                        String sql = "SELECT * FROM Tower WHERE deleted = 0 AND IdT=" + idTower;
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next() == false) {
                            System.out.println("Tower dengan id itu tidak ditemukan");
                            System.out.print("Masukkan Id Tower yang lainnya: ");
                            idTower = sc.next();
                        } else
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
                displayUser();
                System.out.print("NIK Pengelola: ");
                String nik = sc.next();
                while (true) {
                    try {
                        if (nik.equals(".")) {
                            printHomePageAdmin();
                            return;
                        }
                        String sql = "SELECT * FROM [User] WHERE deleted = 0 AND NIK = '" + nik + "'";
                        ResultSet rsNIK = statement.executeQuery(sql);
                        if (rsNIK.next()) {
                            sql = "SELECT * FROM Pengelola WHERE nikPengelola='" + nik + "' AND IdT = " + idTower;
                            ResultSet resultSet = statement.executeQuery(sql);
                            if (resultSet.next()) {
                                System.out.println("NIK tersebut sudah menjadi pengelola untuk Tower itu");
                                System.out.println("Masukkan '.' untuk kembali ke homepage");
                                System.out.print("Masukkan NIK yang lainnya:");
                                nik = sc.next();
                            } else
                                break;
                        } else {
                            System.out.println("User dengan NIK tersebut tidak dapat ditemukan");
                            System.out.println("Masukkan '.' untuk kembali ke homepage");
                            System.out.print("Masukkan NIK yang lainnya:");
                            nik = sc.next();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
                try {
                    String sql = "INSERT INTO Pengelola (IdT, nikPengelola) VALUES ('" + idTower
                            + "', '" + nik + "')";
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Pengelola telah didaftar");
            }
            System.out.println("Ketik apapun untuk kembali kepada homepage");
            sc.next();
            printHomePageAdmin();
        }
        printHomePageAdmin();
    }

    public void deleteEntitas() {
        while (true) {
            printAsciiBox(logo);
            System.out.println("Silahkan pilih entitas apa yang akan didelete");
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
                displayUser();
                System.out.println("Masukkan NIK user");
                System.out.print("NIK: ");
                String nikInput = sc.next();
                if (nikInput.equals("."))
                    continue;
                try {
                    String sql = "UPDATE [User] SET deleted = 1 WHERE NIK = '" + nikInput + "'";
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("User telah didelete");
            } else if (input.equals("2")) {
                displayAllSarusun();
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan Id Sarusun");
                System.out.print("Id Sarusun: ");
                String inputSarusun = sc.next();
                if (inputSarusun.equals("."))
                    continue;
                try {
                    String sql = "UPDATE Sarusun SET deleted = 1 WHERE IdS = '" + inputSarusun + "'";
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Sarusun telah didelete");
            } else if (input.equals("3")) {
                displayTower();
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan Id Tower");
                System.out.print("Id Tower: ");
                String towerInput = sc.next();
                if (towerInput.equals("."))
                    continue;
                try {
                    String sql = "UPDATE Tower SET deleted = 1 WHERE IdT = '" + towerInput + "'";
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Tower telah didelete");
            } else if (input.equals("4")) {
                displayPerangkat();
                System.out.println("Ketik '.' untuk memilih entitas lainnya");
                System.out.println("Masukkan nomor serial perangkat");
                System.out.print("Nomor Serial : ");
                String inputNoSerial = sc.next();
                System.out.println();
                if (inputNoSerial.equals("."))
                    continue;
                try {
                    String sql = "UPDATE Perangkat SET deleted = 1 WHERE noSerial = '" + inputNoSerial + "'";
                    statement.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Perangkat telah didelete");
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
                    + " WHERE Sarusun.deleted = 0 AND nikPemilik = '" + NIK + "' OR Sarusun.IdT = Kelola.IdT";
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
        ResultSet resultSet = null;
        try {
            String sql = "SELECT IdS, nama, Sarusun.Lantai FROM Sarusun JOIN Tower ON Sarusun.IdT = Tower.IdT WHERE Sarusun.deleted = 0";
            resultSet = statement.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println();
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
    }

    public void displayUser() {
        System.out.println();
        String sql = "SELECT NIK, nama FROM [User] WHERE deleted = 0";
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
        String sql = "SELECT * FROM Tower WHERE deleted = 0";
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

    public void displayPerangkat() {
        System.out.println();
        String sql = "SELECT noSerial, IdS FROM Perangkat WHERE deleted = 0";
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println("No.Serial           IdS");

            while (resultSet.next()) {
                String noSerial = resultSet.getString("noSerial");
                int IdS = resultSet.getInt("IdS");
                System.out.printf("%-20s%s\n", noSerial, IdS);
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