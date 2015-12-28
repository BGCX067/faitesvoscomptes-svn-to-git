package cal.accountapp.gestion;


import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class Ajout extends Activity implements OnClickListener {
	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ajouter);
		
        Button bt= (Button)findViewById(R.id.okAjout);
        
        RadioButton rb1=(RadioButton)findViewById(R.id.RadioButton01);
        RadioButton rb2=(RadioButton)findViewById(R.id.RadioButton02);
        
        //changement de langue
        bt.setText(getResources().getString(R.string.menuAdd));
        rb1.setText(getResources().getString(R.string.radioIncome));
        rb2.setText(getResources().getString(R.string.radioIOutgoings));
        bt.setOnClickListener(this);
    }
    
    @SuppressWarnings("static-access")
	public void onClick(View arg0) {
		String raison="";
		String sMontant=getResources().getString(R.string.intentAddAmount);
		boolean flag=false;
		EditText edt;
		RadioButton chb=(RadioButton)findViewById(R.id.RadioButton01);
		edt=(EditText)findViewById(R.id.montant);
		if(edt.getText().toString().equals(""))flag=true;
		else	Main.montant=Double.parseDouble(edt.getText().toString());
		
		edt=(EditText)findViewById(R.id.raison);
		raison=edt.getText().toString();
		if(raison.equals(""))flag=true;
		
		DateFormat df=new DateFormat();
		
		String date = ((String)df.format(getResources().getString(R.string.DateFormate), new Date()));
		
		
		
		if(flag==false)
		{
			if(chb.isChecked()==false)
			{
				sMontant+=" ";
				Main.montant=-Main.montant;
			}
			else sMontant+=" +";
			Main.db.insertOutgoing(sMontant+Main.montant, getResources().getString(R.string.intentAddReason)+" "+raison, " ; Date : "+date);
		}
		else Main.montant=0;
		this.finish();
	}
	
}
