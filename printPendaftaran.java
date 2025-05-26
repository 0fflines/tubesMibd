  public static void printPendaftaran() {
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
                  
                    String sql = "INSERT INTO [User] (NIK, nama, alamatDomisili, noTelp, OTP) VALUES ('" + nikInput + "', '"+ namaInput + "', '"+ alamatInput + "', '"+ noTelpInput + "', '"+ otp + "')";
                    resultSet = statement.executeQuery(sql);          
               
                    System.out.println("User telah didaftar");
                } else if (input.equals("2")) {
                    displayTower();
                    System.out.println("Ketik '.' untuk memilih entitas lainnya");
                    System.out.println("Masukkan data sarusun");
                    System.out.print("Id Tower: ");
                    String towerInput = sc.next();
                    if(towerInput.equals("."))
                        continue;
                    System.out.print("Lantai: ");
                    String lantaiInput = sc.next();
                    displayUser();
                    System.out.print("NIK Pemilik: ");
                    String pemilikInput = sc.next();
                    System.out.println();
                    String sql = "INSERT INTO Sarusun (Lantai, penggunaanAirH, penggunaanAirB, penggunaanAirT, nikPemilik, IdT) VALUES ('" + lantaiInput + "', '"+ 0 + "', '"+ 0  + 0 + "', '"+ pemilikInput + "', '"+ towerInput + "')";
                    resultSet = statement.executeQuery(sql);    
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
                    String sql = "INSERT INTO Tower (IdT, nama) VALUES ('" + towerInput + "', '"+ pengelolaInput"')";
                    resultSet = statement.executeQuery(sql);  
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
                    displaySarusun(0);
                    System.out.print("Id Sarusun lokasi perangkat: ");
                    String sarusunInput = sc.next();
                    System.out.println();
                    String sql = "INSERT INTO Perangkat (noSerial, IdS) VALUES ('" + towerInput + "', '"+ sarusunInput"')";
                    resultSet = statement.executeQuery(sql);  
                    System.out.println("Perangkat telah didaftar");
                }
                System.out.println("Ketik apapun untuk kembali kepada homepage");
                sc.next();
                printHomePageAdmin();
            }
        }
