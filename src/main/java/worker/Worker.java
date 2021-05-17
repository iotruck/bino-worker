package worker;

import okhttp3.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Worker {

    public static void main(String[] args) {

        new Thread(t1).start();
        new Thread(t2).start();
        new Thread(t3).start();
    }

    private static Runnable t1 = new Runnable() {

        public void run() {

            try {

                updateLocation("rota1", 1);

            } catch (Exception e) {

            }

        }

    };

    private static Runnable t2 = new Runnable() {

        public void run() {

            try {

                updateLocation("rota2", 2);

            } catch (Exception e) {

            }

        }

    };

    private static Runnable t3 = new Runnable() {

        public void run() {

            try {

                updateLocation("rota3", 3);

            } catch (Exception e) {

            }

        }

    };

    public static void putLocation(Integer idLocation, String address, Double latitude, Double longitude) throws IOException, InterruptedException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        String json = String.format(Locale.US,"{\r\n    \"id\": %d,\r\n    \"address\": \"%s\",\r\n    \"latitude\": %f,\r\n    \"longitude\": %f\r\n}", idLocation, address, latitude, longitude);
        RequestBody body = RequestBody.create(json,mediaType);
        System.out.println(json);
        Request request = new Request.Builder()
                .url("http://localhost:8080/location/" + idLocation)
                .method("PUT", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();

        Thread.sleep(200);

    }

    static int i = 0;

    private static void countMe(String name) {
        i++;
        System.out.println("Contador atual é: " + i + ", Atualizado pela trhead: " + name);
    }

    public static void updateLocation(String nomeArquivo,Integer idLocation) {

        FileReader arq = null;
        Scanner entrada = null;
        boolean deuRuim = false;
        nomeArquivo += ".csv";
        try {

            arq = new FileReader(nomeArquivo);
            entrada = new Scanner(arq).useDelimiter(";|\\r\\n");

        } catch (FileNotFoundException erro) {

            System.err.println("Arquivo não encontrado");

            System.exit(1);

        }
        try {

            while (entrada.hasNext()) {

                String latitude = entrada.next();
                String longetude = entrada.next();
                //System.out.println(new Location(idLocation, nomeArquivo, Double.valueOf(latitude), Double.valueOf(longetude)));
                putLocation(idLocation, nomeArquivo, Double.valueOf(latitude), Double.valueOf(longetude));
                countMe(nomeArquivo);

            }

        } catch (NoSuchElementException erro) {

            System.err.println("Arquivo com problemas.");
            deuRuim = true;

        } catch (IllegalStateException erro) {

            System.err.println("Erro na leitura do arquivo.");
            deuRuim = true;

        } catch (InterruptedException | IOException e) {

            e.printStackTrace();

        } finally {

            entrada.close();

            try {

                arq.close();

            } catch (IOException erro) {

                System.err.println("Erro ao fechar arquivo.");
                deuRuim = true;

            }
            if (deuRuim) {

                System.exit(1);

            }
        }
    }
}
