import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQResultSequence;
import java.io.File;
import java.util.Scanner;

/**
 * Created by bayer on 22/03/17.
 */
public class Main {

    private static final String collection = "lbayer";

    public static void main(String[] args) {
        Manager man = new Manager();
        Scanner sc = new Scanner(System.in);
        Collection col = null;
        try {
            while(true){
                System.out.println("----------------------------------------------------");
                System.out.println("1. Crear col·lecció i afegir recurs");
                System.out.println("2. Obtenir recurs i imprimir per pantalla");
                System.out.println("3. Realitzar consulta en /mondial/");
                System.out.println("4. Realitzar una consulta en tota la col·lecció");
                System.out.println("5. Sortir del programa");
                System.out.print("----> Opció: ");
                int option = sc.nextInt();
                switch(option){
                    case 1:
                        // Obté la Collection, si no existeix la creara.
                        col = man.getCollection("db/"+collection);

                        // Afegir el recurs a la Collection anteriorment seleccionada.
                        File f = new File("mondial.xml");
                        man.putResource(col, f, "mondial.xml", "XMLResource");
                        break;

                    case 2:
                        if(col != null) {
                            // Obté el recurs i l'imprimeix per pantalla
                            Resource resource = col.getResource("mondial.xml");
                            System.out.println(resource.getContent());
                        }else{
                            System.out.println("La col·lecció no ha sigut seleccionada, executa el PRIMER PAS.");
                        }
                        break;

                    case 3:
                        // Realitza una consulta en la Collection
                        XQResultSequence result = man.query("collection('"+collection+"')/mondial/country/name/text()");
                        while(result.next()){
                            System.out.println(result.getItemAsString(null));
                        }
                        break;

                    case 4:
                        // Realitza una consulta en tota la Collection
                        XQResultSequence result1 = man.query("collection('"+collection+"')//country");
                        while(result1.next()){
                            System.out.println(result1.getItemAsString(null));
                        }
                        break;

                    case 5:
                        System.exit(1);
                        break;
                }
                System.out.println("\n");
            }
        }catch(XMLDBException e){
            e.printStackTrace();
        }catch(XQException e){
            e.printStackTrace();
        }
    }
}
