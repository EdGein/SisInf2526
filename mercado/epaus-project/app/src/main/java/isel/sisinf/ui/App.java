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
import isel.sisinf.model.ContactoCliente;
import isel.sisinf.model.Portefolio;
import isel.sisinf.model.Posicao;
import jakarta.persistence.OptimisticLockException;

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
            System.out.println("3. Create Portefolio");
            System.out.println("4. List Positions");
            System.out.println("5. Update Investments");
            System.out.println("6. Update Client");
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
                System.in.read();
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
        System.out.println("--- Criar Cliente e Contacto ---");
        Scanner s = getScanner();

        System.out.print("NIF: ");
        String nif = s.next();
        System.out.print("Cartão de Cidadão: ");
        String cc = s.next();
        s.nextLine(); // Consumir nova linha
        System.out.print("Nome: ");
        String nome = s.nextLine();
        System.out.print("Tipo de Contacto (Email/Telefone): ");
        String tipo = s.next();
        System.out.print("Contacto (Email ou Número): ");
        String contactoVal = s.next();
        s.nextLine();
        System.out.print("Descrição do Contacto: ");
        String desc = s.nextLine();

        // Dal implementa AutoCloseable — o try-with-resources fecha o EntityManager automaticamente
        try (Dal dal = new Dal()) {
            ContactoCliente ccEnt = new ContactoCliente();
            ccEnt.setNif(nif);
            ccEnt.setCartaoCidadao(cc);
            ccEnt.setNome(nome);
            ccEnt.setTipoContacto(tipo);
            ccEnt.setContacto(contactoVal);
            ccEnt.setDescricao(desc);

            dal.criarClienteComContacto(ccEnt);
            System.out.println("Cliente e contacto criados com sucesso através da vista!");
        } catch (Exception e) {
            System.err.println("Erro ao criar cliente: " + e.getMessage());
        }
    }

    private void createPortfolio() {
        System.out.println("--- Criar Portefólio ---");
        Scanner s = getScanner();
        System.out.print("NIF do Cliente: ");
        String nif = s.next();
        s.nextLine();
        System.out.print("Nome do Portefólio: ");
        String nomePortefolio = s.nextLine();

        try (Dal dal = new Dal()) {
            Portefolio p = dal.criarPortefolio(nif, nomePortefolio);
            System.out.println("Portefólio '" + p.getNome() + "' criado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.err.println("Cliente não encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao criar portefólio: " + e.getMessage());
        }
    }

    private void listPositions() {
        System.out.println("--- Listar Posições do Cliente ---");
        Scanner s = getScanner();
        System.out.print("NIF do Cliente: ");
        String nif = s.next();

        // A navegação lazy (portefólios e posições) deve ocorrer DENTRO do bloco try,
        // enquanto o EntityManager (e o contexto de persistência) ainda estão abertos.
        try (Dal dal = new Dal()) {
            List<Portefolio> portefolios = dal.listarPortefoliosPorNif(nif);

            System.out.println("Portefólios do cliente " + nif + ":");
            for (Portefolio p : portefolios) {
                System.out.println("\nPortefólio: " + p.getNome() + " | Valor Total: " + p.getValorTotal() + " EUR");
                System.out.println("----------------------------------------------------------------------");
                System.out.printf("%-15s | %-12s | %-12s | %-12s\n", "ISIN", "Quantidade", "Valor Atual", "Total Posição");
                System.out.println("----------------------------------------------------------------------");

                for (Posicao pos : p.getPosicoes()) {
                    BigDecimal valorActual = pos.getInstrumento().getDadosFundamentais().getValorActual();
                    BigDecimal totalPosicao = pos.getQuantidade().multiply(valorActual);
                    System.out.printf("%-15s | %-12.4f | %-12.2f | %-12.2f\n",
                            pos.getInstrumento().getInstrumentoId(),
                            pos.getQuantidade(),
                            valorActual,
                            totalPosicao
                    );
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Cliente não encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao listar posições: " + e.getMessage());
        }
    }

    private void updateInvestments() {
        System.out.println("--- Atualizar Valores Diários (Consolidação de Triplos) ---");
        System.out.print("Deseja executar o procedimento de consolidação de triplos? (S/N): ");
        Scanner s = getScanner();
        String confirmacao = s.next();

        if (!confirmacao.equalsIgnoreCase("S")) {
            System.out.println("Operação cancelada.");
            return;
        }

        // Dal.actualizaValorDiario() chama internamente CALL p_actualizaValorDiario()
        // e gere a transação. O try-with-resources fecha o EntityManager no final.
        try (Dal dal = new Dal()) {
            dal.actualizaValorDiario();
            System.out.println("Consolidação efetuada com sucesso na base de dados!");
        } catch (Exception e) {
            System.err.println("Erro ao executar consolidação: " + e.getMessage());
        }
    }

    private void updateClient() {
        System.out.println("--- Atualizar Cliente (Bloqueio Otimista) ---");
        Scanner s = getScanner();
        System.out.print("NIF do Cliente: ");
        String nif = s.next();
        s.nextLine(); // Consumir nova linha
        System.out.print("Novo Nome: ");
        String novoNome = s.nextLine();

        // Dal.atualizarCliente() gere internamente a transação e lança
        // OptimisticLockException se houver conflito de versão.
        try (Dal dal = new Dal()) {
            dal.atualizarCliente(nif, novoNome);
            System.out.println("Cliente atualizado com sucesso!");
        } catch (OptimisticLockException e) {
            System.err.println("[ERRO DE CONCORRÊNCIA] Não foi possível atualizar o cliente.");
            System.err.println("Os dados deste cliente foram alterados por outro utilizador em simultâneo.");
            System.err.println("Por favor, recarregue os dados e tente novamente.");
        } catch (IllegalArgumentException e) {
            System.err.println("Cliente não encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    private void about()
    {
        // TODO: Change the code and your Group ID & member names
        System.out.println("Brought to you by the amazing Isel group of students:");
        System.out.println("Rafael Martins: 36250");
        System.out.println("Bruno Gomes: xxxxx");
        System.out.println("Luís Vasconcelos: xxxxx");
        
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
