package MastersProject.DBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import MastersProject.Models.UpliftedNotification;

public class ImportUplift {
	
	/**
	 * Table names
	 */
	private String sender = "SenderUplift";
	private String app = "AppUplift";
	private String subject = "SubjectUplift";
	private String body = "BodyUplift";
	private String date = "DateUplift";
	

	EntityManagerFactory factory;
	EntityManager em;
	private static final String PERSISTENCE_UNIT_NAME = "informationBead";

	public ArrayList<UpliftedNotification> importFromExcel() throws IOException{

	    factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();
		
		ArrayList<UpliftedNotification> list = new ArrayList<UpliftedNotification>();
		XSSFRow row;
		FileInputStream fis = new FileInputStream( new File("uplift.xlsx"));
	    XSSFWorkbook workbook = new XSSFWorkbook(fis);
	    XSSFSheet spreadsheet = workbook.getSheetAt(0);
	    Iterator < Row > rowIterator = spreadsheet.iterator();
	    
	    /**
	     * TODO: Needs to be changed to get more than one notification
	     */
	    int i = 0;
	    while (i<3) 
	    {
	    	row = spreadsheet.getRow(i);
        	i++;
        	UpliftedNotification notif = new UpliftedNotification();
        	notif.setNotificationId(i);
        	Cell cell = row.getCell(1); 
            notif.setSender(cell.getStringCellValue());
            notif.setSenderRank(getRank(sender, notif.getSender()));
            cell = row.getCell(3);
        	notif.setApp(cell.getStringCellValue());
            notif.setAppRank(getRank(app, notif.getApp()));
        	cell = row.getCell(5);
        	notif.setSubject(cell.getStringCellValue());
            notif.setSubjectRank(getRank(subject, notif.getSubject()));
        	cell = row.getCell(7);
        	notif.setBody(cell.getStringCellValue());
            notif.setBodyRank(getRank(body, notif.getBody()));
        	cell = row.getCell(8);
        	notif.setDate(cell.getDateCellValue());
        	cell = row.getCell(9);
        	notif.setDateImportance(cell.getStringCellValue());
            notif.setDateRank(getRank(date, notif.getDateImportance()));
        	
            list.add(notif);
	    }
	    System.out.println("MastersProject.DBHelper: size of list "+list.size());
	    fis.close();
	    em.close();
		
    	return list;
	}
	
	/**
	 * TODO: use prepared statement
	 * @param value
	 * @return
	 */
	private int getRank(String table, String value){
		Query q = em.createQuery("select t.rank from "+table+" t where t.value = '"+value+"'");
		int result = (int) q.getSingleResult();
		return result;
	}
}
