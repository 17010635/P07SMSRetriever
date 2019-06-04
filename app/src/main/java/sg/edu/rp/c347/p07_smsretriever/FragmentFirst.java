package sg.edu.rp.c347.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentFirst extends Fragment {

    EditText etNum;
    TextView tvSMSFrag1;
    Button btnRetrieve1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        tvSMSFrag1 = view.findViewById(R.id.tvSMSF1);
        btnRetrieve1 = view.findViewById(R.id.btnRetrieveSmsF1);
        etNum = view.findViewById(R.id.etNumber);

       btnRetrieve1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               int permissionCheck = PermissionChecker.checkSelfPermission
                       (getContext(), Manifest.permission.READ_SMS);

               if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                   requestPermissions(new String[]{Manifest.permission.READ_SMS}, 0);
                   // stops the action from proceeding further as permission not
                   //  granted yet
                   return;
               }

               Uri uri = Uri.parse("content://sms");
               // The columns we want
               //  date is when the message took place
               //  address is the number of the other party
               //  body is the message content
               //  type 1 is received, type 2 sent
               String[] reqCols = new String[]{"date", "address", "body", "type"};

               // Get Content Resolver object from which to
               //  query the content provider
               ContentResolver cr = getActivity().getContentResolver();

               // The filter String
               String filter="address LIKE ?";
               // The matches for the ?
               String[] filterArgs = {"%"+etNum.getText().toString()+"%"};

               // Fetch SMS Message from Built-in Content Provider
               Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
               String smsBody = "";
               if (cursor.moveToFirst()) {
                   do {
                       long dateInMillis = cursor.getLong(0);
                       String date = (String) DateFormat
                               .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                       String address = cursor.getString(1);
                       String body = cursor.getString(2);
                       String type = cursor.getString(3);
                       if (type.equalsIgnoreCase("1")) {
                           type = "Inbox:";
                       } else {
                           type = "Sent:";
                       }
                       smsBody += type + " " + address + "\n at " + date
                               + "\n\"" + body + "\"\n\n";
                   } while (cursor.moveToNext());
               }
               tvSMSFrag1.setText(smsBody);
           }
       });

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnRetrieve1.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getContext(), "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}