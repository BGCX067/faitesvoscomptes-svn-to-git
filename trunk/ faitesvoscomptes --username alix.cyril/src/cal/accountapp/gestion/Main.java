package cal.accountapp.gestion;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
//import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
//import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class Main extends ListActivity {
	
	public static DBAdapter db;
	public static  double oldSolde=0.0;
	public static double  montant;
	private static NotificationManager notificationManager;
	private static Notification nouvelleNotif;
	public static File fl;
	public String mainCurrency="";

	
	//String mont="";
	//String rais="";
	
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        File cd = this.getDir("fvc", Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
        fl = new File(cd.getAbsolutePath()+"/properties");
        Log.i("",fl.getAbsolutePath());
        Properties prop=new Properties();
		prop.GoProperties(Properties.MODE_LEC, fl);
		if(Properties.currency.equals("e")==false &&
			Properties.currency.equals("d")==false&&
			Properties.currency.equals("l")==false)Properties.currency="e";
		
		if(Properties.currency.equals("e")==true)	mainCurrency="€";
		else if(Properties.currency.equals("d")==true)	mainCurrency="$";
		else if(Properties.currency.equals("l")==true)	mainCurrency="£";
		
		prop.GoProperties(Properties.MODE_ECR, fl);
		
		
        changeLangage();
        setContentView(R.layout.main);
        getListView().setOnCreateContextMenuListener(this);

        TextView edtn=(TextView)findViewById(R.id.solde);
        edtn.setText(getResources().getString(R.string.yourCurrentBalance)+" ");
        
        db = new DBAdapter(this);
        
        DataBind();
        calculBaseSolde();
        editSolde();
        
    }
    
	private void changeLangage() {
		
    	Locale n= Locale.getDefault();
    	String loc=n.getDisplayLanguage();
		Resources res = getResources();
		Configuration cfg = res.getConfiguration();
		
    	switch(Properties.forceLang)
    	{
    	case Properties.SETTINGS_LANG_EN:
    		
    		cfg.locale=Locale.ENGLISH;
    		
    		break;
    	case Properties.SETTINGS_LANG_FR:
    		
    		cfg.locale=Locale.FRANCE;
    		
    		break;
    	case Properties.SETTINGS_LANG_NO:
    	case 0:
    		
        	if(loc.startsWith("fran")==true)cfg.locale=Locale.FRANCE;
        	else cfg.locale=Locale.ENGLISH;
    		
    		break;
    	default:
    		cfg.locale=Locale.ENGLISH;
    	}

		res.updateConfiguration(cfg, res.getDisplayMetrics());
	}

	private void notifyUser() {
    	/*************GESTION DE LA NOTIFICATION***************/
		//////Pour la notification
		DecimalFormat df = new DecimalFormat ( ) ; 
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		
		notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		switch(Properties.currency.charAt(0))
		{
		case 'e':
			if(Properties.enablenotifSeuil==false)nouvelleNotif = new Notification(R.drawable.img_euro_icon_small_ve,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			else if((Properties.seuilNotifValue*2)<=oldSolde)nouvelleNotif = new Notification(R.drawable.img_euro_icon_small_ve,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			else if(Properties.seuilNotifValue<oldSolde &&
					(Properties.seuilNotifValue*2)>oldSolde )nouvelleNotif = new Notification(R.drawable.img_euro_icon_small_or,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			else if(Properties.seuilNotifValue>=oldSolde)nouvelleNotif = new Notification(R.drawable.img_euro_icon_small_red,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			break;
		case 'l':
			if(Properties.enablenotifSeuil==false)nouvelleNotif = new Notification(R.drawable.img_livre_icon_small_ve,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			else if((Properties.seuilNotifValue*2)<=oldSolde)nouvelleNotif = new Notification(R.drawable.img_livre_icon_small_ve,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			else if(Properties.seuilNotifValue<oldSolde &&
					(Properties.seuilNotifValue*2)>oldSolde )nouvelleNotif = new Notification(R.drawable.img_livre_icon_small_or,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			else if(Properties.seuilNotifValue>=oldSolde)nouvelleNotif = new Notification(R.drawable.img_livre_icon_small_red,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			break;
		case 'd':
			if(Properties.enablenotifSeuil==false)nouvelleNotif = new Notification(R.drawable.img_dollar_icon_small_ve,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			else if((Properties.seuilNotifValue*2)<=oldSolde)nouvelleNotif = new Notification(R.drawable.img_dollar_icon_small_ve,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			else if(Properties.seuilNotifValue<oldSolde &&
					(Properties.seuilNotifValue*2)>oldSolde )nouvelleNotif = new Notification(R.drawable.img_dollar_icon_small_or,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			else if(Properties.seuilNotifValue>=oldSolde)nouvelleNotif = new Notification(R.drawable.img_dollar_icon_small_red,getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency,System.currentTimeMillis());
			break;			
		}
		
		nouvelleNotif.flags=Notification.FLAG_NO_CLEAR;
		
		nouvelleNotif.contentView = new RemoteViews(getPackageName(),R.layout.notified);
		
	
		Intent activity = new Intent(this,Main.class);
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0, activity, 0);
		nouvelleNotif.contentIntent = pendingintent;
	
	

	
		nouvelleNotif.contentView.setTextViewText(R.id.textNotified, getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency);
		nouvelleNotif.contentView.setTextViewText(R.id.monapp, getResources().getString(R.string.app_name));
		if(Properties.enable_notification==true)
		{
			notificationManager.notify(-1, nouvelleNotif);
		}
		else notificationManager.cancelAll();
			
			
		System.gc();
/**FIN***********GESTION DE LA NOTIFICATION***************/
		
	}

	private void calculBaseSolde()
    {
		db.open();
    	Cursor start=db.getOutgoing();
    	String sSolde;
    	start.moveToFirst();
    	oldSolde=0;
    	if(start.getCount()==0)return;
    	do{
    		sSolde=start.getString(start.getColumnIndex("montant"));
    		/*if(sSolde.indexOf(":")!=-1)*/
    		sSolde=sSolde.substring((getResources().getString(R.string.intentAddAmount)+" ").length());
    		oldSolde+=Double.parseDouble(sSolde);
    	}while (start.moveToNext()!=false);
	}

	public void DataBind() {
		db.open();
		Cursor c= db.getOutgoing();
		//clean la DB
		/*c.moveToFirst();
		if(c.getCount()>0)
    	{
			String dTmp="";
			dTmp=c.getString(c.getColumnIndex("montant"));
			if(dTmp.indexOf(":")!=-1)
			{
				CleanDBAdapter CLEANdb = new CleanDBAdapter(this);
				CLEANdb.open();
				String mont="";
				String rais="";
				String date="";
			
			
	    		do{
	    			dTmp=c.getString(c.getColumnIndex("montant"));
	    			mont=dTmp.substring(dTmp.indexOf(":")+2);
	    			dTmp=c.getString(c.getColumnIndex("raison"));
	    			rais=dTmp.substring(dTmp.indexOf(":")+2);
	    			dTmp=c.getString(c.getColumnIndex("date"));
	    			date=dTmp.substring(dTmp.indexOf(":")+2);
	    			
	    			CLEANdb.insertOutgoing(mont, rais, date);
	    		}while (c.moveToNext()!=false);
		    		
				db.Truncate();
				Cursor c2=CLEANdb.getOutgoing();
				
				if(c2.getCount()>0)
		    	{
					c2.moveToFirst();
		    		do{
		    			mont=c.getString(c.getColumnIndex("montant"));
		    			rais=c.getString(c.getColumnIndex("raison"));
		    			date=c.getString(c.getColumnIndex("date"));
		    			
		    			db.insertOutgoing(mont, rais, date);
		    		}while (c.moveToNext()!=false);
		    	}
				CLEANdb.Truncate();
				CLEANdb.close();
	    	}
    	}*/
		/////////////
		startManagingCursor(c);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(	this,
																R.layout.list_item,
																c,
																new String[]{"montant","raison","date"},
																new int[]{R.id.lvMontant,R.id.lvRaison, R.id.lvDate}
															);
		setListAdapter(adapter);
	}

	protected void onDestroy() {
    	db.close();
    	super.onDestroy();
    	android.os.Process.killProcess(android.os.Process.myPid());
    }

	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,100,0,getResources().getString(R.string.menuClearAll)).setIcon(R.drawable.croix);
		menu.add(0,200,0,getResources().getString(R.string.menuAdd)).setIcon(R.drawable.ok);
		menu.add(0,300,0,getResources().getString(R.string.parametreName)).setIcon(android.R.drawable.ic_menu_manage);
		menu.add(0,400,2,getResources().getString(R.string.recurrentName)).setIcon(android.R.drawable.ic_menu_revert);
		menu.add(0,500,3,"Export").setIcon(R.drawable.exportcsv);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 100:
			deleteAlertDialog();
			break;
		case 200:
			Intent addintent = new Intent(this,Ajout.class);
			this.startActivityForResult(addintent,1);
			break;
		case 300:
			Intent settingintent = new Intent(this,Seting.class);
			this.startActivityForResult(settingintent,1);
			break;
		case 400:
			Intent recintent = new Intent(this,Recurrent.class);
			this.startActivityForResult(recintent,1);
			break;
		case 500:
			dialog_CSV_PDF();
			break;
		}
		return true;
	}
	
	private int tmpFormat;
	private Context mContext;
	private void dialog_CSV_PDF() {
		mContext=this;
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("TEST");
		ad.setSingleChoiceItems(new String[]{"Format CSV","Format PDF"}, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				tmpFormat=which;
			}
    	});
		
		ad.setPositiveButton("Ok", 
			new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					switch (tmpFormat) {
					case 0:
						db.open();
				    	Cursor start=db.getOutgoing();
				    	String data="";
				    	String dTmp="";
				    		File exportFile=new File("/sdcard/","export.csv");
					    	try{
					    		FileOutputStream fos = new FileOutputStream(exportFile);
					    		DataOutputStream dos = new DataOutputStream(fos);
					    		start.moveToFirst();
					    		data=getResources().getString(R.string.intentAddAmount)+";"+getResources().getString(R.string.intentAddReason)+";Date:;\n";
					    		dos.write(data.getBytes());
						    	if(start.getCount()!=0)
						    	{
						    		do{
						    			dTmp=start.getString(start.getColumnIndex("montant"));
						    			data=dTmp.substring(dTmp.indexOf(":")+2)+" "+mainCurrency+";";
						    			dTmp=start.getString(start.getColumnIndex("raison"));
						    			data+=dTmp.substring(dTmp.indexOf(":")+2)+";";
						    			dTmp=start.getString(start.getColumnIndex("date"));
						    			data+=dTmp.substring(dTmp.indexOf(":")+2)+";\n";
						    			dos.write(data.getBytes());
						    		}while (start.moveToNext()!=false);
						    	}
						    	dos.close();
						    	Toast.makeText(mContext, getResources().getString(R.string.savedSD)+".csv", Toast.LENGTH_SHORT).show();
					    	}catch(Exception eop){
					    		Toast.makeText(mContext, getResources().getString(R.string.missingSD), Toast.LENGTH_SHORT).show();
					    	}
						break;
					case 1:
						db.open();
				    	Cursor start2=db.getOutgoing();
				    	String data2="";
				    	String dTmp2="";
				    		File exportFile2=new File("/sdcard/","export.pdf");
					    	try{
					    		FileOutputStream fos2 = new FileOutputStream(exportFile2);
					    		DataOutputStream dos2 = new DataOutputStream(fos2);
					    		
					    		pdfHeader(dos2);
					    		
					    		String col1="BT\n/F1 11 Tf\n70 ";
					    		String col2="BT\n/F1 11 Tf\n200 ";
					    		String col3="BT\n/F1 11 Tf\n330 ";
					    		String mid=" Td\n(";
					    		String end=") Tj\nET\n";
					    		
					    		String dataTMP="";
					    		
					    		int coordMid=700;
					    		
					    		data2=col1+coordMid+mid+getResources().getString(R.string.intentAddAmount)+end+"\n";
					    		data2+=col2+coordMid+mid+getResources().getString(R.string.intentAddReason)+end+"\n";
					    		data2+=col3+coordMid+mid+"Date:"+end+"\n";
					    		dos2.write(data2.getBytes());
					    		
					    		
					    		start2.moveToFirst();
					    		data2=getResources().getString(R.string.intentAddAmount)+";"+getResources().getString(R.string.intentAddReason)+";Date:;\n";
					    		dos2.write(data2.getBytes());
						    	if(start2.getCount()!=0)
						    	{
						    		do{
						    			coordMid-=20;
						    			
						    			dTmp2=start2.getString(start2.getColumnIndex("montant"));
						    			dataTMP=dTmp2.substring(dTmp2.indexOf(":")+2)+" "+mainCurrency;
						    			
						    			data2=col1+coordMid+mid+dataTMP+end+"\n";
							    		
						    			dTmp2=start2.getString(start2.getColumnIndex("raison"));
						    			dataTMP=dTmp2.substring(dTmp2.indexOf(":")+2);
						    			
						    			data2+=col2+coordMid+mid+dataTMP+end+"\n";
						    			
						    			dTmp2=start2.getString(start2.getColumnIndex("date"));
						    			dataTMP=dTmp2.substring(dTmp2.indexOf(":")+2)+";\n";
						    			
						    			data2+=col3+coordMid+mid+dataTMP+end+"\n";
						    			
						    			dos2.write(data2.getBytes());
						    		}while (start2.moveToNext()!=false);
						    	}
						    	pdfFooter(dos2);
						    	dos2.close();
						    	Log.i("CLOSE", "CLOSE");
						    	Toast.makeText(mContext, getResources().getString(R.string.savedSD)+".pdf", Toast.LENGTH_SHORT).show();
					    	}catch(Exception eop){
					    		Toast.makeText(mContext, getResources().getString(R.string.missingSD), Toast.LENGTH_SHORT).show();
					    	}
						break;
					default:
						break;
					}
				}

				private void pdfHeader(DataOutputStream dos2) throws IOException {
					String d="";
					d="%PDF-1.4\n1 0 obj\n<< /Type /Catalog\n/Outlines 2 0 R\n/Pages 3 0 R\n>>\nendobj\n";
					d+="2 0 obj\n<< /Type Outlines\n/Count 0\n>>\nendobj\n";
					d+="3 0 obj\n<< /Type /Pages\n/Kids [4 0 R]\n/Count 1\n>>\nendobj\n";
					d+="4 0 obj\n<< /Type /Page\n/Parent 3 0 R\n/MediaBox [0 0 612 792]\n/Contents 5 0 R\n/Resources << /ProcSet 6 0 R\n/Font << /F1 7 0 R >>\n>>\n>>\nendobj\n";
					d+="5 0 obj\n<< /Length 200 >>\nstream\n";
					dos2.write(d.getBytes());
				}

				private void pdfFooter(DataOutputStream dos2) throws IOException {
					String d="";
					d="endstream\nendobj\n";
					d+="6 0 obj\n[/PDF /Text]\nendobj\n7 0 obj\n<< /Type /Font\n/Subtype /Type1\n/Name /F1\n";
					d+="/BaseFont /Helvetica\n/Encoding /MacRomanEncoding\n>>\nendobj\nxref\n0 8\n0000000000 65535 f\n";
					d+="0000000009 00000 n\n0000000074 00000 n\n0000000120 00000 n\n0000000179 00000 n\n0000000364 00000 n\n";
					d+="0000000466 00000 n\n0000000496 00000 n\ntrailer\n<< /Size 8\n/Root 1 0 R\n>>\nstartxref\n";
					d+="625\n%%EOF\n";
					dos2.write(d.getBytes());
					
				}
			}
		);
		
		ad.setNegativeButton("Cancel", 
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						
					}
				}
			);
			
			ad.setOnCancelListener(new DialogInterface.OnCancelListener(){
				public void onCancel(DialogInterface dialog) {
					// OK				
				}}
			);
		
		ad.show();
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		 if(keyCode == KeyEvent.KEYCODE_BACK) finish();
		return super.onKeyDown(keyCode, event);
	}
	
	private void deleteAlertDialog() {
		
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setIcon(R.drawable.croix);
		ad.setTitle(getResources().getString(R.string.menuClearAll)+" ?");
		ad.setPositiveButton("Yes", 
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						
						
						db.Truncate();
						oldSolde=0.0;
						DataBind();
						editSolde();
					}
				}
			);
			
			ad.setNegativeButton("No", 
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
					}
				}
			);
			
			ad.setOnCancelListener(new DialogInterface.OnCancelListener(){
				public void onCancel(DialogInterface dialog) {		
				}}
			);
			
			ad.show();
	}

	public static void calculSolde() {
		oldSolde+=montant;
	}
	public void editSolde()
	{
		montant=0.0;
		TextView tv=(TextView)findViewById(R.id.solde);
		DecimalFormat df = new DecimalFormat ( ) ; 
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		tv.setText(getResources().getString(R.string.currentBalance)+" "+df.format(oldSolde)+" "+mainCurrency+".");
		
		
		//pour la notification :
		notifyUser();

	}
	
	
	
	 // Creation du menu contextuel
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Action");
		menu.add(0,100,0,getResources().getString(R.string.onSelectMenuSupp));
		//menu.add(0,200,0,getResources().getString(R.string.onSelectMenuModif));
	}
    
	 // Selection d'un item du menu contextuel
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
		case 100:
			db.deleteOutgoing(info.id);
			calculBaseSolde();
			DataBind();
	        editSolde();
			break;
		/*case 200:
			db.open();
			Cursor mC=db.getOutgoing();
			
			String date;
			mC.moveToFirst();
	    	if(mC.getCount()!=0)
	    	{
	    		for(int i=0; i<info.position;i++)mC.moveToNext();
	    		
	    		AlertDialog.Builder ad = new AlertDialog.Builder(this);
	    		ad.setTitle(getResources().getString(R.string.modifyExpense));
	    		ad.setView(LayoutInflater.from(this).inflate(R.layout.modifexpense,null));
	    		final EditText edtmontant=(EditText)findViewById(R.id.modifMontant);
	    		final EditText edtraison =(EditText)findViewById(R.id.modifRaison);
	    		ad.setPositiveButton("Yes", 
	    			new android.content.DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int arg1) {
	    					mont=edtmontant.getText().toString();
	    					rais=edtraison.getText().toString();
	    				}
	    			}
	    		);
	    		
	    		ad.setNegativeButton("Cancel", 
	    			new android.content.DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int arg1) {
	    					//OK
	    				}
	    			}
	    		);
	    		
	    		ad.setOnCancelListener(new DialogInterface.OnCancelListener(){
	    			public void onCancel(DialogInterface dialog) {
	    				// OK				
	    			}}
	    		);
	    		
	    		ad.show();
	    		
	    		
	    		
	    		if(mont.equals("")==false && rais.equals("")==false)
	    		{
	    			date=mC.getString(mC.getColumnIndex("date"));
	    			db.deleteOutgoing(info.id);
	    			db.insertOutgoing(mont, rais, date);
	    		}
	    	}
			calculBaseSolde();
			DataBind();
	        editSolde();
			break;		*/
		}
		
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		switch(resultCode&0x000F){
		case Properties.NOTIFICATION_DISABLED:
			Properties.enable_notification=false;
			break;
		case Properties.NOTIFICATION_ENABLED:
			Properties.enable_notification=true;
			break;
		}
		
		switch(resultCode&0x00F0)
		{
    	case Properties.SETTINGS_LANG_EN:
    		if(Properties.forceLang!=Properties.SETTINGS_LANG_EN)
    		{
    			Properties.forceLang=Properties.SETTINGS_LANG_EN;
    			Toast.makeText(this, getResources().getString(R.string.pleaseRestartApp), Toast.LENGTH_LONG).show();
    		}
    		break;
    	case Properties.SETTINGS_LANG_FR:
    		if(Properties.forceLang!=Properties.SETTINGS_LANG_FR)
    		{
    			Properties.forceLang=Properties.SETTINGS_LANG_FR;
    			Toast.makeText(this, getResources().getString(R.string.pleaseRestartApp), Toast.LENGTH_LONG).show();
    		}
    		break;
    	case Properties.SETTINGS_LANG_NO:
    		if(Properties.forceLang!=Properties.SETTINGS_LANG_NO)
    		{
    			Properties.forceLang=Properties.SETTINGS_LANG_NO;
        		Toast.makeText(this, getResources().getString(R.string.pleaseRestartApp), Toast.LENGTH_LONG).show();
    		}
    		break;
		}
		Properties prop=new Properties();
		prop.GoProperties(Properties.MODE_ECR,fl);
		
		if(Properties.currency.equals("e")==true)	mainCurrency="€";
		else if(Properties.currency.equals("d")==true)	mainCurrency="$";
		else if(Properties.currency.equals("l")==true)	mainCurrency="£";
		
		calculBaseSolde();
		DataBind();
		editSolde();
		super.onActivityResult(requestCode, resultCode, data);
		
	}
}