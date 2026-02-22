package ProjetoClimatico;

import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

//metodo 1 cria interface com user
//metodo 2 busca dados da API
//metodo 3 imprime dados organizados
public class Climatico {
    // MÉTODO 0 — main faz a interface com o usuário
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);// cria um objeto scanner para ler a entrada do usuário

        System.out.print("Digite o nome da cidade: ");
        String cidade = scanner.nextLine();// lê a cidade digitada pelo usuário

        try {
            String dadosClimaticos = getDadosClimaticos(cidade);// chama o método getDadosClimaticos

            if (dadosClimaticos.contains("\"error\"")) {// verifica se a cidade foi encontrada
                System.out.println("Cidade não encontrada.");
            } else {
                imprimirDadosClimaticos(dadosClimaticos);// chama o método imprimirDadosClimaticos
            }

        } catch (Exception e) {// captura exceções
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // MÉTODO 1 — busca dados da API
    public static String getDadosClimaticos(String cidade) throws Exception {// recebe a cidade como parâmetro

        String apiKey = Files.readString(Path.of("apikey.txt")).trim();// lê a chave da API
        String cidadeFormatada = URLEncoder.encode(cidade, StandardCharsets.UTF_8);// formata a cidade

        String apiUrl = "http://api.weatherapi.com/v1/current.json?key="
                + apiKey + "&q=" + cidadeFormatada;// monta a URL da API

        HttpRequest request = HttpRequest.newBuilder()// cria um objeto HttpRequest
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();// cria um cliente http
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());// envia a requisição

        return response.body();// retorna o corpo da resposta
    }

    // MÉTODO 2 — imprime dados organizados
    public static void imprimirDadosClimaticos(String dados) {// recebe os dados da API como parâmetro

        JSONObject json = new JSONObject(dados);// cria um objeto json

        JSONObject location = json.getJSONObject("location");
        JSONObject current = json.getJSONObject("current");
        JSONObject condition = current.getJSONObject("condition");

        String cidade = location.getString("name");
        String pais = location.getString("country");
        String condicao = condition.getString("text");

        double temp = current.getDouble("temp_c");
        double sensacao = current.getDouble("feelslike_c");
        int umidade = current.getInt("humidity");
        double vento = current.getDouble("wind_kph");
        double pressao = current.getDouble("pressure_mb");
        String dataHora = current.getString("last_updated");

        System.out.println("\nClima em " + cidade + " - " + pais);
        System.out.println("Atualizado em: " + dataHora);
        System.out.println("Condição: " + condicao);
        System.out.println("Temperatura: " + temp + "°C");
        System.out.println("Sensação térmica: " + sensacao + "°C");
        System.out.println("Umidade: " + umidade + "%");
        System.out.println("Vento: " + vento + " km/h");
        System.out.println("Pressão: " + pressao + " mb");
    }
}