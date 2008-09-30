package util;

public class DACPConstants extends daap.DAAPConstants {
	
	public static final int cmst = 1668117364; //dmcp.status
	public static final int mstt = 1836282996;
	public static final int cmsr = 1668117362; //playback status revision number
	public static final int caps = 1667330163;
	public static final int cash = 1667330920;
	public static final int carp = 1667330672;
	public static final int caas = 1667326323; //shuffle? seen 2 when shuffle was on
	public static final int caar = 1667326322; //repeat? seen 6 when repeat was off
	public static final int canp = 1667329648; //only when playing 16 bytes
	public static final int cann = 1667329646;
	public static final int cana = 1667329633;
	public static final int canl = 1667329644;
	public static final int cang = 1667329639;
	public static final int asai = 1634951529;
	public static final int cmmk = 1668115819;
	public static final int cant = 1667329652; //remaining time, millis
	public static final int cast = 1667330932; //total time, millis
	public static final int assr = 1634956146;
	public static final int muty = 1836414073;
	public static final int mtco = 1836344175;
	public static final int mrco = 1836213103;
	public static final int mlcl = 1835819884;
	public static final int mlit = 1835821428;
	public static final int apso = 1634759535;
	
	
	public static final int msrv = parseInt("msrv");
	public static final int mpro = parseInt("mpro");
	public static final int apro = parseInt("apro");
	public static final int mlog = parseInt("mlog");
	public static final int mlid = parseInt("mlid");
	public static final int mdcl = parseInt("mdcl");
	public static final int minm = parseInt("minm");
	public static final int mupd = parseInt("mupd");
	public static final int musr = parseInt("musr");
	
	public static final int avdb = parseInt("avdb");
	public static final int abro = parseInt("abro");
	public static final int abar = parseInt("abar");
	public static final int agal = parseInt("agal"); //daap.albumgroup?
	
	public static final int mper = parseInt("mper");
	public static final int mimc = parseInt("mimc");
	public static final int mctc = parseInt("mctc");
	public static final int miid = parseInt("miid");
	
	public static final int caci = parseInt("caci"); //dmcp.ctrl-int
	
	//all boolean flags. What do they mean?
	//c: control
	//m/a: media, audio
	//s: setting?
	public static final int cmik = parseInt("cmik");
	public static final int cmsp = parseInt("cmsp");
	public static final int cmsv = parseInt("cmsv");
	public static final int cass = parseInt("cass");
	public static final int casu = parseInt("casu");
	
	public static final int cmgt = parseInt("cmgt"); //something to do with volume - GeT property?
	public static final int cmvo = parseInt("cmvo"); //dmcp.volume? includes integer volume 0-100?
	
	public static final int casp = parseInt("casp"); //dacp.speakers maybe
	public static final int msma = parseInt("msma"); //no idea (long, 0 in itunes)
	public static final int caia = parseInt("caia"); //boolean (true for itunes) is available?
	
	public static final int cmcp = parseInt("cmcp"); //control prompt update response
	
	//these nodes are used for indexes
	public static final int mshl = parseInt("mshl"); //list, composed of mlit nodes
	public static final int mshc = parseInt("mshc"); //two bytes - range? second is ascii letter
	public static final int mshi = parseInt("mshi"); //index
	public static final int mshn = parseInt("mshn"); //number of items
	
	/*
	public static final int cmst = parseInt("cmst");
	public static final int mstt = parseInt("mstt");
	public static final int cmsr = parseInt("cmsr");
	public static final int caps = parseInt("caps");
	public static final int cash = parseInt("cash");
	public static final int carp = parseInt("carp");
	public static final int caas = parseInt("caas");
	public static final int caar = parseInt("caar");
	public static final int canp = parseInt("canp");
	public static final int cann = parseInt("cann");
	public static final int cana = parseInt("cana");
	public static final int canl = parseInt("canl");
	public static final int cang = parseInt("cang");
	public static final int asai = parseInt("asai");
	public static final int cmmk = parseInt("cmmk");
	public static final int cant = parseInt("cant");
	public static final int cast = parseInt("cast");
	public static final int assr = parseInt("assr");
	
	public static final int muty = parseInt("muty");
	public static final int mtco = parseInt("mtco");
	public static final int mrco = parseInt("mrco");
	public static final int mlcl = parseInt("mlcl");
	public static final int mlit = parseInt("mlit");
	
	public static final int apso = parseInt("apso");
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		for (Field f: DACPConstants.class.getFields()) {
			System.out.println("\tpublic static final int " + f.getName() + " = " + f.get(new DACPConstants())+ ";");
		}
	}
	*/
}
