package cal.accountapp.gestion;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBAdapter {

	DatabaseHelper	DBHelper;
	Context			context;
	SQLiteDatabase	db;
	
	public DBAdapter(Context context){
		this.context = context;
		DBHelper = new DatabaseHelper(context);
	}	
	
	public class DatabaseHelper extends SQLiteOpenHelper{

		Context			context;
		
		public DatabaseHelper(Context context) {
			super(context, "outGoing", null, 1);
			this.context = context;
		}


		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table outGoing (_id integer primary key autoincrement, "
					+ "montant text not null, raison text not null, date text not null " 
					+ ");");			
		}


		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Toast.makeText(context, "Mise à jour de la Base de données version "+oldVersion+" vers "+newVersion, Toast.LENGTH_SHORT).show();
			db.execSQL("DROP TABLE IF EXISTS outGoing");
			onCreate(db);
		}
		
	}
	
	public DBAdapter open(){
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		db.close();
	}
	
	public void Truncate(){
		db.execSQL("DELETE FROM outGoing");
	}
	
	public long insertOutgoing(String montant, String raison, String date){
		ContentValues values = new ContentValues();
		values.put("montant", montant);
		values.put("raison", raison);
		values.put("date", date);
		return db.insert("outGoing", null, values);
	}
	
	
	public boolean deleteOutgoing(long id){
		return db.delete("outGoing", "_id="+id, null)>0;
	}
	
	public Cursor getOutgoing(){

		return db.query("outGoing", new String[]{
				"_id",
				"montant",
				"raison",
				"date"}, null, null, null, null, "_id DESC");
	}
	
}
