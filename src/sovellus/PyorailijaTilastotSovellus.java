package sovellus;

import java.util.Map;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PyorailijaTilastotSovellus extends Application {

    @Override
    public void start(Stage ikkuna) {

        PyorailijaTilasto tilasto = new PyorailijaTilasto("helsingin-pyorailijamaarat.csv");

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.add(new Label("Valitse tarkasteltava paikka"), 0, 0);

        ObservableList<String> data = FXCollections.observableArrayList();
        data.addAll(tilasto.paikat());

        ListView<String> lista = new ListView<>(data);
        gridPane.add(lista, 0, 1);

        CategoryAxis xAkseli = new CategoryAxis();
        NumberAxis yAkseli = new NumberAxis();
        xAkseli.setLabel("Vuosi / Kuukausi");
        yAkseli.setLabel("Pyöräilijöitä");
        yAkseli.setTickLabelsVisible(false);

        BarChart<String, Number> kaavio = new BarChart<>(xAkseli, yAkseli);

        kaavio.setLegendVisible(false);

        lista.setOnMouseClicked((MouseEvent event) -> {
            String valittu = lista.getSelectionModel().getSelectedItem();
            Map<String, Integer> arvot = tilasto.pyorailijoitaKuukausittain(valittu);
            kaavio.getData().clear();
            XYChart.Series chartData = new XYChart.Series();

            arvot.keySet().stream().forEach(aika -> {
                chartData.getData().add(new XYChart.Data(aika, arvot.get(aika)));
            });

            kaavio.getData().add(chartData);
        });

        gridPane.add(kaavio, 1, 0, 1, 2);
        Scene nakyma = new Scene(gridPane);

        ikkuna.setScene(nakyma);
        ikkuna.show();
    }

    public static void main(String[] args) {
        launch(PyorailijaTilastotSovellus.class);
    }

}

/*
 CategoryAxis xAkseli = new CategoryAxis();
 NumberAxis yAkseli = new NumberAxis(30.0, 90, 3);
 yAkseli.setTickLabelsVisible(false);
 yAkseli.setLabel("Suurempi parempi!");

 BarChart<String, Number> pylvaskaavio = new BarChart<>(xAkseli, yAkseli);

 pylvaskaavio.setTitle("Liittymän nopeus");
 pylvaskaavio.setLegendVisible(false);

 XYChart.Series asukasluvut = new XYChart.Series();
 asukasluvut.getData().add(new XYChart.Data("NDA", 77.4));
 asukasluvut.getData().add(new XYChart.Data("Tomera", 77.2));
 asukasluvut.getData().add(new XYChart.Data("Liisa", 77.1));
 asukasluvut.getData().add(new XYChart.Data("Ratiolinja", 77.1));

 pylvaskaavio.getData().add(asukasluvut);
 Scene nakyma = new Scene(pylvaskaavio, 400, 300);
 ikkuna.setScene(nakyma);
 ikkuna.show();

 */

/*
ackage sovellus;
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
 
public class PyorailijaTilasto {
 
    private List<String> rivit;
 
    public PyorailijaTilasto(String tiedosto) {
        try {
            rivit = Files.lines(Paths.get(tiedosto)).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException ex) {
            throw new RuntimeException("Tiedoston " + tiedosto + " lukeminen epäonnistui. Virhe: " + ex.getMessage());
        }
    }
 
    public List<String> paikat() {
        List<String> paikat = Arrays.stream(rivit.get(0).split(";")).collect(Collectors.toList());
        return paikat.subList(1, paikat.size());
    }
 
    public Map<String, Integer> pyorailijoitaKuukausittain(String paikka) {
        List<String> paikat = paikat();
        if (paikat.indexOf(paikka) < 0) {
            return new HashMap<>();
        }
 
        Map<String, List<Integer>> mittaArvotKuukausittain = new TreeMap<>();
 
        int indeksi = paikat.indexOf(paikka) + 1;
        rivit.stream().map(merkkijono -> merkkijono.split(";"))
                .filter(taulukko -> taulukko.length > 10)
                .forEach(taulukko -> {
                    String[] pvmTaulukko = taulukko[0].split(" ");
                    if (pvmTaulukko.length < 3) {
                        return;
                    }
 
                    String kuukausi = pvmTaulukko[3] + " / " + merkkijonoKuukaudenNumeroksi(pvmTaulukko[2]);
 
                    mittaArvotKuukausittain.putIfAbsent(kuukausi, new ArrayList<>());
 
                    int maara = 0;
                    if (!taulukko[indeksi].isEmpty()) {
                        maara = Integer.parseInt(taulukko[indeksi]);
                    }
 
                    mittaArvotKuukausittain.get(kuukausi).add(maara);
                });
 
        Map<String, Integer> pyorailijoidenLukumaarat = new TreeMap<>();
        mittaArvotKuukausittain.keySet().stream().forEach(arvo -> {
            pyorailijoidenLukumaarat.put(arvo, mittaArvotKuukausittain.get(arvo).stream().mapToInt(a -> a).sum());
        });
 
        return pyorailijoidenLukumaarat;
    }
 
    private String merkkijonoKuukaudenNumeroksi(String kuukausi) {
        List<String> kuukaudet = Arrays.asList("tammi", "helmi", "maalis", "huhti", "touko", "kesä", "heinä", "elo", "syys", "loka", "marras", "joulu");
        int numero = kuukaudet.indexOf(kuukausi) + 1;
 
        if (numero < 10) {
            return "0" + numero;
        }
 
        return "" + numero;
    }
}
src/sovellus/PyorailijaTilastotSovellus.java
?
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
	
package sovellus;
 
import java.util.Map;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
 
public class PyorailijaTilastotSovellus extends Application {
 
    @Override
    public void start(Stage ikkuna) {
 
        PyorailijaTilasto tilasto = new PyorailijaTilasto("helsingin-pyorailijamaarat.csv");
 
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
 
        gridPane.add(new Label("Valitse tarkasteltava paikka"), 0, 0);
 
        ObservableList<String> data = FXCollections.observableArrayList();
        data.addAll(tilasto.paikat());
 
        ListView<String> lista = new ListView<>(data);
        gridPane.add(lista, 0, 1);
 
        CategoryAxis xAkseli = new CategoryAxis();
        NumberAxis yAkseli = new NumberAxis();
        xAkseli.setLabel("Vuosi / Kuukausi");
        yAkseli.setLabel("Pyöräilijöitä");
 
         
        BarChart<String, Number> kaavio = new BarChart<>(xAkseli, yAkseli);
        kaavio.setAnimated(false);
        kaavio.setLegendVisible(false);
 
        lista.setOnMouseClicked((MouseEvent event) -> {
            String valittu = lista.getSelectionModel().getSelectedItem();
            Map<String, Integer> arvot = tilasto.pyorailijoitaKuukausittain(valittu);
            kaavio.getData().clear();
            XYChart.Series chartData = new XYChart.Series();
 
            arvot.keySet().stream().forEach(aika -> {
                chartData.getData().add(new XYChart.Data(aika, arvot.get(aika)));
            });
 
            kaavio.getData().add(chartData);
        });
 
        gridPane.add(kaavio, 1, 0, 1, 2);
 
        Scene nakyma = new Scene(gridPane);
 
        ikkuna.setScene(nakyma);
        ikkuna.show();
    }
 
    public static void main(String[] args) {
        launch(PyorailijaTilastotSovellus.class);
    }
 
}

*/