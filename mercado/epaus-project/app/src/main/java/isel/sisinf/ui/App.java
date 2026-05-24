/*
MIT License

Copyright (c) 2025-2026, Nuno Datia, Matilde Pato, ISEL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package isel.sisinf.ui;

import isel.sisinf.jpa.Dal;
import isel.sisinf.model.Cliente;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;

/**
 * 
 * Didactic material to support 
 * to the curricular unit of 
 * Introduction to Information Systems 
 *
 * The examples may not be complete and/or totally correct.
 * They are made available for teaching and learning purposes and 
 * any inaccuracies are the subject of debate.
 */

interface DbWorker
{
    void doWork();
}
class UI implements AutoCloseable
{
    private enum Option
    {
        // DO NOT CHANGE ANYTHING!
        Unknown,
        Exit,
        createClient,
        createPortfolio,
        listPositions,
        updateInvestments,
        updateClient,
        about
    }
    private static UI __instance = null;
    private static Scanner __s = null;
  
    private HashMap<Option,DbWorker> __dbMethods;

    private UI()
    {
        // DO NOT CHANGE ANYTHING!
        __dbMethods = new HashMap<Option,DbWorker>();
        __dbMethods.put(Option.createClient, () -> UI.this.createClient());
        __dbMethods.put(Option.createPortfolio, () -> UI.this.createPortfolio()); 
        __dbMethods.put(Option.listPositions, () -> UI.this.listPositions());
        __dbMethods.put(Option.updateInvestments, () -> UI.this.updateInvestments());
        __dbMethods.put(Option.updateClient, () ->  UI.this.updateClient());
        __dbMethods.put(Option.about, new DbWorker() {public void doWork() {UI.this.about();}});
    }

    public static UI getInstance()
    {
        // DO NOT CHANGE ANYTHING!
        if(__instance == null)
        {
            __instance = new UI();
        }
        return __instance;
    }

    public static Scanner getScanner()
    {
        if(__s == null)
        {
            __s = new Scanner(System.in);
        }
        return __s;
    }

    private Option DisplayMenu()
    {
        Option option = Option.Unknown;
        Scanner s = getScanner();
        try
        {
            // DO NOT CHANGE ANYTHING!
            System.out.println("  ___ ___                 ");
            System.out.println(" | __| _ \\__ _ _  _ ___  ");
            System.out.println(" | _||  _/ _` | || (_-<  ");
            System.out.println(" |___|_| \\__,_|\\_,_/__/  ");
            System.out.println("        Management DEMO   ");
            System.out.println();
            System.out.println("1. Exit");
            System.out.println("2. Create Client");
            System.out.println("3. List Existing Costumer");
            System.out.println("4. List Docks");
            System.out.println("5. Start Trip");
            System.out.println("6. Park Scooter");
            System.out.println("7. About");
            System.out.print(">");
            int result = s.nextInt();
            option = Option.values()[result];
        }
        catch(RuntimeException ex)
        {
            //nothing to do.
        }
        
        return option;

    }
    private static void clearConsole() throws Exception
    {
        // DO NOT CHANGE ANYTHING!
        for (int y = 0; y < 25; y++) //console is 80 columns and 25 lines
            System.out.println("\n");
    }

    public void Run() throws Exception
    {
        // DO NOT CHANGE ANYTHING!
        Option userInput;
        do
        {
            clearConsole();
            userInput = DisplayMenu();
            clearConsole();
            try
            {
                __dbMethods.get(userInput).doWork();
                getScanner().nextLine();
                //System.in.read();
            }
            catch(NullPointerException ex)
            {
                //Nothing to do. The option was not a valid one. Read another.
            }

        }while(userInput!=Option.Exit);
    }

    /**
    To implement from this point forward. 
    -------------------------------------------------------------------------------------     
        IMPORTANT:
    --- DO NOT MESS WITH THE CODE ABOVE. YOU JUST HAVE TO IMPLEMENT THE METHODS BELOW ---
    --- Other Methods and properties can be added to support implementation. 
    ---- Do that also below                                                         -----
    -------------------------------------------------------------------------------------
    
    */


    //Implement an AutoClosable object. 
    // If needed you can add more stuff to clean at the end
    @Override
    public void close()
    {
        if(__s != null)
        {
            __s.close();
            __s = null;
        }
    }

    private void createClient() {
        System.out.println("NIF, CC, Nome, Tipo(email/telefone), Contacto, Descrição (separados por espaço):");
        String nif = getScanner().next(); String cc = getScanner().next(); String nome = getScanner().next();
        String tipo = getScanner().next(); String contacto = getScanner().next(); String desc = getScanner().next();
        try (Dal dal = new Dal()) {
            dal.beginTransaction();
            dal.criarClienteComContacto(nif, cc, nome, tipo, contacto, desc);
            dal.commit();
            System.out.println("Criado com sucesso.");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void createPortfolio() {
        System.out.println("NIF e Nome do Portfólio:");
        String nif = getScanner().next(); String nome = getScanner().next();
        try (Dal dal = new Dal()) {
            dal.beginTransaction(); dal.criarPortfolio(nif, nome); dal.commit();
            System.out.println("Criado com sucesso.");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void listPositions() {
        System.out.println("NIF do cliente:");
        String nif = getScanner().next();
        try (Dal dal = new Dal()) {
            List<Object[]> pos = dal.listarPosicoes(nif);
            for (Object[] p : pos) {
                System.out.printf("Portfólio: %s | ISIN: %s | Qtd: %s | Valor: %s | Var: %s%%%n", p[0], p[1], p[2], p[3], p[4]);
            }
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void updateInvestments() {
        System.out.println("ISIN e Novo Valor:");
        String isin = getScanner().next(); BigDecimal valor = getScanner().nextBigDecimal();
        try (Dal dal = new Dal()) {
            dal.beginTransaction(); dal.atualizarValorDiario(isin, valor); dal.commit();
            System.out.println("Atualizado com sucesso.");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void updateClient() {
        System.out.println("NIF do cliente a atualizar:");
        String nif = getScanner().next();
        try (Dal dal = new Dal()) {
            Cliente c = dal.encontrarCliente(nif);
            if (c == null) return;
            System.out.println("Novo nome:");
            c.setNome(getScanner().next());

            System.out.println("[TESTE] Altere o cliente na BD agora e prima ENTER...");
            getScanner().nextLine();

            dal.beginTransaction(); dal.atualizarCliente(c); dal.commit();
            System.out.println("Atualizado com sucesso.");
        } catch (RollbackException | OptimisticLockException e) {
            System.out.println("ERRO DE CONCORRÊNCIA: O registo foi modificado por outro utilizador.");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void about()
    {
        // TODO: Change the code and your Group ID & member names
        System.out.println("Criado por:");
        System.out.println("Criado por:");
        System.out.println("Criado por:");
        System.out.println("Criado por:");
        System.out.println("DAL version:"+ isel.sisinf.jpa.Dal.version());
        System.out.println("Core version:"+ isel.sisinf.model.Core.version());
        
    }
}

public class App{
    public static void main(String[] args) throws Exception{
       try(UI ui = UI.getInstance())
        {
            ui.Run();
        }
    }
}
