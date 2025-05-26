  public static void displayUser() {
            System.out.println();
            String sql = "SELECT * FROM USER";
            resultSet = statement.executeQuery(sql);  

            while (resultSet.next()) {

                String NIK = resultSet.getInt("NIK");
               String nama = resultSet.getString("nama");
               String alamat = resultSet.getString("alamatDomisili"); 
               String noTelp = resultSet.getString("noTelp"); 
               String OTP = resultSet.getString("OTP"); 
              
            
                System.out.println(NIK + " " + nama+ " " + alamat + " " + noTelp + " " + OTP);                  
            }
            System.out.println();
        }

        public static void displayTower(){
            System.out.println();
            System.out.println("Tower yang terdaftar:");
            String sql = "SELECT * FROM TOWER";
                    resultSet = statement.executeQuery(sql);  

                    while (resultSet.next()) {
    
                        int idTower = resultSet.getInt("idT");
                       String nama = resultSet.getString("nama");
                      
                    
                        System.out.println(idTower + " " + nama);                  
                    }
            System.out.println();
        }
