package fr.ensim.interop.introrest.model.telegram;

public class Joke {
    private int id;
    private String titre;
    private String texte;
    private int rate;

    public Joke(){
        this.id=0;
    }

    public Joke(int id, String titre, String texte, int rate) {
        this.id = id;
        this.titre = titre;
        this.texte = texte;
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Joke{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", texte='" + texte + '\'' +
                ", rate=" + rate +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
