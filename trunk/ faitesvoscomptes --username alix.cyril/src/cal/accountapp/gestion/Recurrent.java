package cal.accountapp.gestion;


import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Recurrent extends ListActivity implements OnClickListener{

	public static RecDBAdapter rdb;
	
	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recurrent);
		((Button)findViewById(R.id.recAddCurrent)).setText(getResources().getString(R.string.addToAccount));
		
		
		RadioButton rb1=(RadioButton)findViewById(R.id.recCred);
        RadioButton rb2=(RadioButton)findViewById(R.id.recDeb);
        rb1.setText(getResources().getString(R.string.radioIncome));
        rb2.setText(getResources().getString(R.string.radioIOutgoings));
		
		getListView().setOnCreateContextMenuListener(this);
		Button saveBtn=(Button)findViewById(R.id.recSave);
		saveBtn.setText(getResources().getString(R.string.savesetting));
		saveBtn.setOnClickListener(this);
		Button addButton=(Button)findViewById(R.id.recAddCurrent);
		addButton.setOnClickListener(this);
		Main.db.close();
		rdb = new RecDBAdapter(this);
		rdb.open();
        DataBind();
		
		
	}

	public void DataBind() {
    	
		Cursor c= rdb.getOutgoing();
		startManagingCursor(c);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(	this,
																R.layout.list_item,
																c,
																new String[]{"montant","raison","date"},
																new int[]{R.id.lvMontant,R.id.lvRaison, R.id.lvDate}
															);
		setListAdapter(adapter);
	}

	
	
	 // Creation du menu contextuel
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Action");
		menu.add(0,100,0,getResources().getString(R.string.onSelectMenuSupp));
		menu.add(0,200,0,getResources().getString(R.string.addToaccountsingle));
	}
    
	 // Selection d'un item du menu contextuel
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
		case 100:
			rdb.deleteOutgoing(info.id);
			DataBind();
			break;	
		case 200:
			Main.db.open();
			Cursor mC=rdb.getOutgoing();
			String mont;
			String rais;
			String date;
			Date nD=new Date();
			mC.moveToFirst();
	    	if(mC.getCount()!=0)
	    	{
	    		for(int i=0; i<info.position;i++)mC.moveToNext();
	    		mont=mC.getString(mC.getColumnIndex("montant"));
	    		rais=mC.getString(mC.getColumnIndex("raison"));
	    		date=mC.getString(mC.getColumnIndex("date"));
	    		date=date+"-"+nD.getMonth()+"-"+(nD.getYear()+1900);
	    		Main.db.insertOutgoing(mont, rais, date);
	    		Toast.makeText(this,getResources().getString(R.string.toastAdded)+" :\n"+mont+", "+rais+", "+date , Toast.LENGTH_SHORT).show();
	    	}
			break;
		}
		return true;
	}
	
	public void onClick(View arg0) {
		switch(arg0.getId())
		{
		case R.id.recSave:
			String raison="";
			String date="";
			String sMontant=getResources().getString(R.string.intentAddAmount);
			double montant=0;
			boolean flag=false;
			EditText edt;
	
			edt=(EditText)findViewById(R.id.edtRecMontant);
			if(edt.getText().toString().equals(""))flag=true;
			else
			{
				montant=Double.parseDouble(edt.getText().toString());
				if(((RadioButton)findViewById(R.id.recDeb)).isChecked()==true)montant=-montant;
				if(montant>0)sMontant=sMontant+" +"+montant;
				else sMontant=sMontant+" "+montant;
			}
			
			edt=(EditText)findViewById(R.id.edtRecRaison);
			raison=edt.getText().toString();
			if(raison.equals(""))flag=true;
			
			edt=(EditText)findViewById(R.id.edtrecjourDuMois);
			if(edt.getText().toString().equals("") 
					|| Integer.parseInt(edt.getText().toString())>31
					|| Integer.parseInt(edt.getText().toString())<1) flag=true;
			else date=edt.getText().toString();
			
			
			if(flag==false)rdb.insertOutgoing(sMontant, getResources().getString(R.string.intentAddReason)+" "+raison, " ; Date : "+date);
			DataBind();
			break;
		case R.id.recAddCurrent:
			addOkDialog();
			break;
		}
	}
	
	private void addOkDialog() {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setIcon(R.drawable.croix);
		ad.setTitle(getResources().getString(R.string.addToAccount)+" ?");
		ad.setPositiveButton("Yes", 
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						
						Main.db.open();
						Cursor mC=rdb.getOutgoing();
						
						String mont;
						String rais;
						String date;
						Date nD=new Date();
						mC.moveToFirst();
				    	if(mC.getCount()==0)return;
				    	do{
				    		mont=mC.getString(mC.getColumnIndex("montant"));
				    		rais=mC.getString(mC.getColumnIndex("raison"));
				    		date=mC.getString(mC.getColumnIndex("date"));
				    		date=date+"-"+nD.getMonth()+"-"+(nD.getYear()+1900);
				    		Main.db.insertOutgoing(mont, rais, date);
				    	}while (mC.moveToNext()!=false);
						finish();
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

	protected void onDestroy() {
    	//rdb.close();
    	super.onDestroy();
    }
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,100,0,getResources().getString(R.string.menuClearAll)).setIcon(R.drawable.croix);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 100:
			deleteAlertDialog();
			break;
		}
		return true;
	}
	
	
	private void deleteAlertDialog() {
		
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setIcon(R.drawable.croix);
		ad.setTitle(getResources().getString(R.string.menuClearAll)+" ?");
		ad.setPositiveButton("Yes", 
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						
						
						rdb.Truncate();
						DataBind();
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
	
}
