package sn.adama.l2gl.app.service;

import sn.adama.l2gl.app.model.Action;
import sn.adama.l2gl.app.model.Comparaison;
import sn.adama.l2gl.app.model.Test;
import sn.adama.l2gl.app.model.Transformation;
import sn.adama.l2gl.app.model.Vehicule;

import java.util.ArrayList;
import java.util.List;

public class ParcAutoService {

    // 1. Filtrer — garde uniquement les véhicules qui passent le test
    public List<Vehicule> filtrerVehicules(List<Vehicule> src, Test<Vehicule> regle) {
        List<Vehicule> resultat = new ArrayList<>();
        for (Vehicule v : src) {
            if (regle.tester(v)) {       // on délègue le test à l'interface
                resultat.add(v);
            }
        }
        return resultat;
    }

    // 2. Mapper — transforme chaque véhicule en une valeur de type R
    public List<String> mapperVehicules(List<Vehicule> src, Transformation<Vehicule, String> f) {
        List<String> resultat = new ArrayList<>();
        for (Vehicule v : src) {
            resultat.add(f.transformer(v)); // on délègue la transformation
        }
        return resultat;
    }

    // 3. Appliquer — exécute une action sur chaque véhicule
    public void appliquerSurVehicules(List<Vehicule> src, Action<Vehicule> action) {
        for (Vehicule v : src) {
            action.executer(v);             // on délègue l'action
        }
    }

    // 4. Trier — trie la liste selon la règle de comparaison
    public void trierVehicules(List<Vehicule> src, Comparaison<Vehicule> cmp) {
        // Tri à bulles classique avec notre interface Comparaison
        int n = src.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (cmp.comparer(src.get(j), src.get(j + 1)) > 0) {
                    // Échanger les deux éléments
                    Vehicule temp = src.get(j);
                    src.set(j, src.get(j + 1));
                    src.set(j + 1, temp);
                }
            }
        }
    }
}