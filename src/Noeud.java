import java.util.ArrayList;

public class Noeud {

	private int LARGEUR_MAX; // A DEFINIR
	protected Noeud parent;
	protected ArrayList<Noeud> enfants; // largeur max ??
	protected ArrayList<Coup> nonVisites;
	protected int joueur;
	protected Coup coup;
	protected Etat etat;
	double recompense;
	int nb_simus;

	public Noeud(Noeud parent, Coup coup,  Etat e) {
		this.parent = parent;
		this.coup = coup;
		this.etat = new Etat();
		this.enfants = new ArrayList<Noeud>();
		this.nonVisites = new ArrayList<Coup>();
		this.recompense = 0;
		this.nb_simus = 0;
		if (parent != null && coup != null) {
			etat.copieEtat(parent.getEtat());
			etat.jouerCoup(coup);
			this.setJoueur(1 - parent.getJoueur());
			//this.setJoueur(etat.autreJoueur(parent.getJoueur()));
		} else {
			etat.copieEtat(e);
			this.setJoueur(e.getJoueur());
			//this.setJoueur(0);
		}
		//update();
	}

	public Noeud getParent() {
		return parent;
	}
	
	public double getRecompense() {
		int res = 0;
		if(this.etat.testFin() == Etat.FinDePartie.NON){
			for(Noeud child:enfants){
				res += child.getRecompense();
			}
			recompense = res;
		}else{			
			update();
		}
		return recompense;
	}
	
	public void setRecompense(int recompense) {
		this.recompense = recompense;
	}
	
	public void backUpRecompense(double d){
		this.recompense += d;
	}

	public void setEtat(Etat e) {
		this.etat = e;
	}

	public Coup getCoup() {
		return coup;
	}

	public void setCoup(Coup c) {
		this.coup = c;
	}

	public void setJoueur(int i) {
		this.joueur = i;
	}

	public void setParent(Noeud n) {
		this.parent = n;
	}

	public void setNbVictoires(int v) {
		this.recompense = v;
	}

	public void setNbSimus(int s) {
		this.nb_simus = s;
	}

	// augmente la taille de enfant[] ??? comme ça ?
	public void setNbEnfantsPlus() {
		this.LARGEUR_MAX += 1;
	}

	public void setNbEnfants(int i) {
		this.LARGEUR_MAX = i;
	}

	public ArrayList<Noeud> getListEnfants(){
		return this.enfants;
	}
	
	public ArrayList<Coup> getListNonVisites(){
		
		return this.nonVisites;
	}
	
	public Noeud getEnfants(int i) {
		return enfants.get(i);
	}

	public void setEnfants(int i, Noeud enf) {
		this.enfants.add(i,enf);
	}

	public Etat getEtat() {
		// TODO Auto-generated method stub
		return etat;
	}

	public int getJoueur() {
		return this.joueur;
	}

	public int getNbEnfants() {
		// TODO Auto-generated method stub
		return enfants.size();
	}
	
	public void incrementeNbSimulation(){
		this.nb_simus++;
	}
	
	public int getNbSimulation(){
		return this.nb_simus;
	}
	
	public void update(){
		Etat.FinDePartie res = this.etat.testFin();
		if(res == Etat.FinDePartie.MATCHNUL || res == Etat.FinDePartie.ORDI_GAGNE){
			this.recompense = 1;
		}else if(res == Etat.FinDePartie.HUMAIN_GAGNE){
			this.recompense = 0;
		}
	}

}
