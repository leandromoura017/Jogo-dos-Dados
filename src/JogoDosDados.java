import java.io.*;
import java.util.*;

class Dado {
    public int rolar() {
        return (int) (Math.random() * 6) + 1;
    }
}

class Player {
    private String nome;
    private int aposta;
    private int vitorias;

    public Player(String nome, int aposta) {
        this.nome = nome;
        this.aposta = aposta;
        this.vitorias = 0;
    }

    public String getNome() {
        return nome;
    }

    public int getAposta() {
        return aposta;
    }

    public int getVitorias() {
        return vitorias;
    }

    public void incrementarVitorias() {
        vitorias++;
    }

    @Override
    public String toString() {
        return nome + " - Vitórias: " + vitorias;
    }

    public void registrarVitoria() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ranking.txt", true))) {
            writer.write(nome + ", " + vitorias);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao registrar vitória: " + e.getMessage());
        }
    }
}

class Game {
    private List<Player> jogadores;
    private Dado dado1;
    private Dado dado2;

    public Game() {
        this.jogadores = new ArrayList<>();
        this.dado1 = new Dado();
        this.dado2 = new Dado();
    }

    public void adicionarJogador(Player jogador) {
        if (jogadores.size() < 11 && !existeApostaDuplicada(jogador)) {
            jogadores.add(jogador);
        } else {
            System.out.println("Não é possível add jogador: " + jogador.getNome());
        }
    }

    public boolean existeApostaDuplicada(Player jogador) {
        for (Player p : jogadores) {
            if (p.getAposta() == jogador.getAposta()) {
                return true;
            }
        }
        return false;
    }

    public void jogar() {
        int resultadoDado1 = dado1.rolar();
        int resultadoDado2 = dado2.rolar();
        int soma = resultadoDado1 + resultadoDado2;

        System.out.println("Resultado dos dados: " + resultadoDado1 + " + " + resultadoDado2 + " = " + soma);
        verificarVencedor(soma);
    }

    private void verificarVencedor(int soma) {
        boolean ganhou = false;
        for (Player jogador : jogadores) {
            if (jogador.getAposta() == soma) {
                System.out.println("O jogador " + jogador.getNome() + " ganhou!");
                jogador.incrementarVitorias();
                ganhou = true;
            }
        }
        if (!ganhou) {
            System.out.println("A máquina ganhou!");
        }

        atualizarRanking();
    }

    private void atualizarRanking() {
        List<Player> ranking = new ArrayList<>(jogadores);
        ranking.sort((p1, p2) -> p2.getVitorias() - p1.getVitorias());
        System.out.println("Ranking:");

        for (int i = 0; i < Math.min(11, ranking.size()); i++) {
            System.out.println((i + 1) + ". " + ranking.get(i));
        }
        salvarRankingEmArquivo(ranking);
    }

    private void salvarRankingEmArquivo(List<Player> ranking) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ranking.txt"))) {
            for (Player jogador : ranking) {
                writer.write(jogador.getNome() + ", " + jogador.getVitorias());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar o ranking: " + e.getMessage());
        }
    }

    public void carregarRanking() {
        // Lê o ranking do arquivo ao iniciar o jogo.
        try (BufferedReader reader = new BufferedReader(new FileReader("ranking.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] dados = line.split(", ");
                String nome = dados[0];
                int vitorias = Integer.parseInt(dados[1]);
                Player player = new Player(nome, 0); // A aposta inicial não importa aqui
                for (int i = 0; i < vitorias; i++) {
                    player.incrementarVitorias();
                }
                jogadores.add(player);
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar ranking: " + e.getMessage());
        }
    }
}

public class JogoDosDados {
    public static void main(String[] args) {
        Game jogo = new Game();
        jogo.carregarRanking(); // Carrega o ranking no início

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Insira o nome do jogador (ou digite 'sair' para encerrar): ");
            String nome = scanner.nextLine();
            if (nome.equalsIgnoreCase("sair")) break;

            System.out.println("Insira sua aposta (1-12): ");
            int aposta = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            if (aposta < 1 || aposta > 12) {
                System.out.println("Aposta inválida. Deve ser entre 1 e 12.");
                continue;
            }

            Player jogador = new Player(nome, aposta);
            jogo.adicionarJogador(jogador);
            jogo.jogar();
        }
        scanner.close();
    }
}
