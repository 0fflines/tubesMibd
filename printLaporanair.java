 String sql = "SELECT penggunaanAirH , penggunaanAirB , penggunaanAirT , FROM Sarusun  WHERE IdS = " +  idInput ;
                    ResultSet resultSet = statement.executeQuery(sql);  
    
            
    while (resultSet.next()) {
    
        int penggunaanAirH = resultSet.getInt("penggunaanAirH");
        int penggunaanAirB = resultSet.getInt("penggunaanAirB");
        int penggunaanAirT = resultSet.getInt("penggunaanAirT");
    
        System.out.println("Penggunaan Air H: " + penggunaanAirH);
        System.out.println("Penggunaan Air B: " + penggunaanAirB);
        System.out.println("Penggunaan Air T: " + penggunaanAirT);
    }
