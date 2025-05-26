public void printPendaftaranPerangkat() {
    printAsciiBox(logo);
    displaySarusun(0);
    System.out.println("Ketik '.' jika ingin kembali ke homepage");
    System.out.print("Masukkan Id Sarusun untuk melihat perangkat: ");
    String idSarusun = sc.next();

    if (idSarusun.equals(".")) {
        printHomePageUser();
        return;
    }

    try {
        String sql = "SELECT noSerial, IdS FROM Perangkat WHERE IdS = '" + idSarusun + "'";
        ResultSet resultSet = statement.executeQuery(sql);

        System.out.println("\nDaftar perangkat pada Sarusun dengan Id " + idSarusun + ":");
        boolean found = false;
        while (resultSet.next()) {
            String noSerial = resultSet.getString("noSerial");
            String idS = resultSet.getString("IdS");
            System.out.println("• Nomor Serial: " + noSerial + ", Id Sarusun: " + idS);
            found = true;
        }
        if (!found) {
            System.out.println("(Tidak ada perangkat yang terdaftar di sarusun ini)");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    System.out.println("\nKetik apapun untuk kembali ke homepage");
    sc.next();
    printHomePageUser();
}
