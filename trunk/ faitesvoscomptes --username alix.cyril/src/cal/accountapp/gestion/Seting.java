package cal.accountapp.gestion;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Seting extends Activity implements OnClickListener, OnCheckedChangeListener{

	int res=Properties.NOTIFICATION_ENABLED;
	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setings);
		
		
		Properties propr=new Properties();
		propr.GoProperties(Properties.MODE_LEC, Main.fl);
		
		//Ici mettre des trucs pour garder les settings en mémoire
		
		CheckBox forceL=(CheckBox)findViewById(R.id.forceLang);
		CheckBox threshold=(CheckBox)findViewById(R.id.enableSeuil);
		
		((TextView)findViewById(R.id.txtDevise)).setText(getResources().getString(R.string.txtDevise));
		
		if(Properties.currency.equals("e")==true)	((TextView)findViewById(R.id.deviseSeuil)).setText("€");
		else if(Properties.currency.equals("d")==true)	((TextView)findViewById(R.id.deviseSeuil)).setText("$");
		else if(Properties.currency.equals("l")==true)	((TextView)findViewById(R.id.deviseSeuil)).setText("£");
		
		((CheckBox)findViewById(R.id.enableNotification)).setChecked(Properties.enableNotifChecked);
		((CheckBox)findViewById(R.id.forceLang)).setChecked(Properties.enableForceLangChecked);
		
		if(Properties.enableForceLangChecked==true)
		{
			((RadioButton)findViewById(R.id.rbEN)).setEnabled(Properties.enableForceLangChecked);
			((RadioButton)findViewById(R.id.rbFR)).setEnabled(Properties.enableForceLangChecked);
			((RadioButton)findViewById(R.id.rbEN)).setChecked(Properties.forceLangENChecked);
			((RadioButton)findViewById(R.id.rbFR)).setChecked(Properties.forceLangFRChecked);
		}
		

		
		if(Properties.currency.equals("e"))
		{
			((RadioButton)findViewById(R.id.sym_euro)).setChecked(true);
			((RadioButton)findViewById(R.id.sym_dolar)).setChecked(false);
			((RadioButton)findViewById(R.id.sym_livre)).setChecked(false);
		}
		else if(Properties.currency.equals("d"))
		{
			((RadioButton)findViewById(R.id.sym_euro)).setChecked(false);
			((RadioButton)findViewById(R.id.sym_dolar)).setChecked(true);
			((RadioButton)findViewById(R.id.sym_livre)).setChecked(false);
		}
		else if(Properties.currency.equals("l"))
		{
			((RadioButton)findViewById(R.id.sym_euro)).setChecked(false);
			((RadioButton)findViewById(R.id.sym_dolar)).setChecked(false);
			((RadioButton)findViewById(R.id.sym_livre)).setChecked(true);
		}
		else
		{
			((RadioButton)findViewById(R.id.sym_euro)).setChecked(true);
			((RadioButton)findViewById(R.id.sym_dolar)).setChecked(false);
			((RadioButton)findViewById(R.id.sym_livre)).setChecked(false);
		}
		forceL.setOnCheckedChangeListener(this);

		threshold.setOnCheckedChangeListener(this);
		
		Button btnSave=(Button)findViewById(R.id.settingSave);
		btnSave.setOnClickListener(this);
		
		((RadioButton)findViewById(R.id.sym_euro)).setOnClickListener(this);
		((RadioButton)findViewById(R.id.sym_dolar)).setOnClickListener(this);
		((RadioButton)findViewById(R.id.sym_livre)).setOnClickListener(this);
		
		

		((CheckBox)findViewById(R.id.enableNotification)).setText(getResources().getString(R.string.autorisernotif));
		((CheckBox)findViewById(R.id.forceLang)).setText(getResources().getString(R.string.forcerLangage));

		((Button)findViewById(R.id.settingSave)).setText(getResources().getString(R.string.savesetting));
		
		((CheckBox)findViewById(R.id.enableSeuil)).setText(getResources().getString(R.string.seuilnotif));
		((CheckBox)findViewById(R.id.enableSeuil)).setChecked(Properties.enablenotifSeuil);
		((EditText)findViewById(R.id.seuilNotification)).setText(""+Properties.seuilNotifValue);
		
	}

	private void enablenotification() {
		res=Properties.NOTIFICATION_ENABLED;
	}

	private void disablenotification() {
		res=Properties.NOTIFICATION_DISABLED;
		
	}

	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.settingSave:
			res=0;
			CheckBox chb=(CheckBox)findViewById(R.id.enableNotification);
			if(chb.isChecked()==false)
			{
				disablenotification();
				Properties.enableNotifChecked=false;
			}
			else
			{
				enablenotification();
				Properties.enableNotifChecked=true;
			}
			
			if(((CheckBox)findViewById(R.id.forceLang)).isChecked()==true)
			{
				if(((RadioButton)findViewById(R.id.rbEN)).isChecked())
				{
					res+=Properties.SETTINGS_LANG_EN;
					Properties.enableForceLangChecked=true;
					Properties.forceLangENChecked=true;
					Properties.forceLangFRChecked=false;
				}
				else if(((RadioButton)findViewById(R.id.rbFR)).isChecked())
				{
					res+=Properties.SETTINGS_LANG_FR;
					Properties.enableForceLangChecked=true;
					Properties.forceLangFRChecked=true;
					Properties.forceLangENChecked=false;
				}
				else
				{
					res+=Properties.SETTINGS_LANG_NO;
					Properties.enableForceLangChecked=false;
					Properties.forceLangFRChecked=false;
					Properties.forceLangENChecked=false;
				}
			}
			else
			{
				res+=Properties.SETTINGS_LANG_NO;
				Properties.enableForceLangChecked=false;
				Properties.forceLangFRChecked=false;
				Properties.forceLangENChecked=false;
			}
			
			
			if( ((RadioButton)findViewById(R.id.sym_euro)) .isChecked()==true)Properties.currency="e";
			if( ((RadioButton)findViewById(R.id.sym_dolar)).isChecked()==true)Properties.currency="d";
			if( ((RadioButton)findViewById(R.id.sym_livre)).isChecked()==true)Properties.currency="l";
			
			if(((EditText)findViewById(R.id.seuilNotification)).getText().toString().equals("")==false)
			{
				Properties.seuilNotifValue=Double.parseDouble(((EditText)findViewById(R.id.seuilNotification)).getText().toString());
			}
			else Properties.seuilNotifValue=0.0;
			this.setResult(res);
			finish();
			break;
		case R.id.sym_dolar:
			((TextView)findViewById(R.id.deviseSeuil)).setText("$");
			break;
		case R.id.sym_euro:
			((TextView)findViewById(R.id.deviseSeuil)).setText("€");
			break;
		case R.id.sym_livre:
			((TextView)findViewById(R.id.deviseSeuil)).setText("£");
			break;
		}
	}

	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch(arg0.getId())
		{
		case R.id.forceLang:
			if(arg0.isChecked()==true)
			{
				((RadioButton)findViewById(R.id.rbEN)).setEnabled(true);
				((RadioButton)findViewById(R.id.rbFR)).setEnabled(true);
			}
			else
			{
				((RadioButton)findViewById(R.id.rbEN)).setEnabled(false);
				((RadioButton)findViewById(R.id.rbFR)).setEnabled(false);
			}
			break;
		case R.id.enableSeuil:
			if(arg0.isChecked()==true)
			{ 
				((EditText)findViewById(R.id.seuilNotification)).setEnabled(true);
				Properties.enablenotifSeuil=true;
			}
			else
			{
				((EditText)findViewById(R.id.seuilNotification)).setEnabled(false);
				Properties.enablenotifSeuil=false;
			}
			((EditText)findViewById(R.id.seuilNotification)).setText(""+Properties.seuilNotifValue);
			break;

		}
		
	}
	
	
}
