
public class Coup {

	protected int ligne;
	protected int colonne;

	public Coup() {

	}

	public Coup(int l, int c) {
		this.ligne = l;
		this.colonne = c;
	}

	public int getLigne() {
		return ligne;
	}

	public void setLigne(int ligne) {
		this.ligne = ligne;
	}

	public int getColonne() {
		return colonne;
	}

	public void setColonne(int colonne) {
		this.colonne = colonne;
	}
}
