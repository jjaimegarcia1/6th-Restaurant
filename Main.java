package com.company;

import java.sql.*;
import java.util.*;
public class Main {
    public static void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (Exception e){
            System.out.println("Can't load driver");
        }

        try{
            System.out.println("Starting Connection........");
            Scanner scan=new Scanner(System.in);

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://161.35.177.175:3306/project6", "jose","p6jj");
            System.out.println("**************************************************");
            System.out.println("****************Hello*****************************");
            System.out.println("**********Welcome to 6th Recipe*******************");

            int choice = ShowMenu(scan);
            while(choice!=4){
                if(choice == 1){
                    ShowAllRecipes(con);
                    int rs=ReturnSelection(scan);
                    while(rs==1) {
                        String name=NameSelector(scan);
                        ShowAllRecipesDetails(con,name);
                        rs=returnback(scan);
                    }
                }
                else if(choice == 2){
                    ShowAllRecipes(con);
                    int rs=ReturnSelection(scan);
                    while(rs==1) {
                        String name=NameSelector(scan);
                        ShowIngredientAmount(con,name);
                        rs=returnback(scan);
                    }

                }
                else if(choice == 3){
                    ShowAllIngredients(con);
                    int rs=ReturnSelection(scan);
                    while(rs==1) {
                        String name=NameSelector(scan);
                        ShowIngredientVendor(con,name);
                        rs=returnback(scan);
                    }

                }
                else{
                    System.out.println("Turning Off, GOODBYE!");
                }
                choice=ShowMenu(scan);
            }
            System.out.println("Turning Off, GOODBYE!");
        }
        catch (SQLException e){
            System.out.println(e.getMessage() + " Can't connect to database");
            while(e!=null){
                System.out.println("Message: "+e.getMessage());
                e= e.getNextException();
            }
        }
        catch (Exception e){
            System.out.println("Did not enter correct information");
        }
    }
    private static int returnback(Scanner scan) {
        System.out.println("---Press (1) to Enter Another, (Any digit) to Return to MAIN MENU");
        int another=Integer.parseInt(scan.nextLine());
        return another;
    }

    private static int ReturnSelection(Scanner scan) {
        System.out.println("Do you wish to Select?"); //show basic recipe info?, then show more information upon selection?
        System.out.println("Press (1) to SELECT, (2) to return to MAIN MENU");//Something with update? Adding a new recipe?
        int back;
        back = Integer.parseInt(scan.nextLine());
        while (back < 1 || back > 2){
            System.out.println("Invalid Selection - Try Again");
            back = Integer.parseInt(scan.nextLine());
        }
        return back;

    }

    private static int ShowMenu(Scanner scan) {
        System.out.println("*************************************************");
        System.out.println("Press 1 To Show Recipe names"); //show basic recipe info?, then show more information upon selection?
        System.out.println("Press 2 to Show Ingredient Amounts for Recipes");//Something with update? Adding a new recipe?
        System.out.println("Press 3  To Show Ingredients");//Show all ingredient information? then allow to see vendor information?
        System.out.println("Press 4 to Exit");
        System.out.println("**************************************************");
        int choice;
        System.out.print("ENTER SELECTION (1-4): ");
        choice = Integer.parseInt(scan.nextLine());
        while (choice < 1 || choice > 4){
            System.out.println("Invalid Selection - Try Again");
            choice =Integer.parseInt(scan.nextLine());
        }
        return choice;

    }

    private static void ShowAllIngredients(Connection con) throws SQLException {
        String query = "select *"
                + " from Ingredient";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        System.out.println("************************************************");
        System.out.println("*********Showing ALL Ingredients****************");
        while(result.next()) {
            System.out.println("Ingredient Name: " + result.getString("Iname"));
        }
        System.out.println("*************************************************");
    }
    private static void ShowAllRecipes(Connection con) throws SQLException {
        String query = "select *"
                + " from Recipe";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        System.out.println("********************************************");
        System.out.println("********Showing All Recipes*****************");
        while(result.next()) {
            System.out.println("Recipe Name: "+ result.getString("rName"));
        }
        System.out.println("**********************************************");
    }
    private static void ShowAllRecipesDetails(Connection con,String name) throws SQLException {
        String query = "select rName, courseType, Serving, Gtype, Vtype"
                + " from Recipe"
                + " Where rName= ? ";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1,name);
        ResultSet result = stmt.executeQuery();
        if (!result.next()) {
            System.out.println("Could not Find Recipe Please Try Again");
        }
        else{
            System.out.println("Showing Recipe Details");
            System.out.println("********************************************");
            System.out.println("NAME: "+ result.getString("rName"));
            System.out.println("What type of course: " + result.getString("courseType"));
            System.out.println("How many people does it serve: "+ result.getString("Serving" )+" People");
            System.out.println(result.getString("Gtype"));
            System.out.println(result.getString("VType"));
            System.out.println("********************************************");
        }
    }
    private static void ShowIngredientAmount(Connection con, String name) throws SQLException {
        String query = "select Recipe.Rid, Recipe.rName, needs.iName, needs.qty, needs.mid"
                + " from Recipe INNER JOIN needs on Recipe.Rid = needs.Rid"
                + " Where rName = ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1,name);
        ResultSet result = stmt.executeQuery();
        if (!result.next()) {
            System.out.println("Could not find Recipe, Please Try Again");
        }
        else{
            System.out.println("Showing Ingredient Qtys for " + name);
            System.out.println("*************************************************************");
            while(result.next()) {
                System.out.println("Ingredient " + result.getString("needs.Iname") + " Quantity " + result.getString("needs.qty")+" "+result.getString("needs.mid"));
            }
            System.out.println("***************************************************************");
            }
    }

    private static void ShowIngredientVendor(Connection con, String name) throws SQLException {
        String query = "select v.Vname, v.Contact"
                + " from Vendor v"
                + " Where v.vid IN"
                + " (Select o.vid from BoughtFrom o"
                + " Where Iname = ?)";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1,name);
        ResultSet result = stmt.executeQuery();
        if (!result.next()) {
            System.out.println("Could not Find Ingredient Please Try Again");
        }
        else{
            System.out.println("Showing Vendor Information");
            System.out.println("************************************************");
            while(result.next()) {
                System.out.println("Vname " + result.getString("Vname"));
                System.out.println("Contact " + result.getString("Contact"));
            }
            System.out.println("************************************************");
        }
    }

    private static String NameSelector(Scanner scan) {
        System.out.println("Enter name");
        String rname=scan.nextLine();
        return rname;
    }
}
