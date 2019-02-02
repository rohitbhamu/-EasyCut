import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.rohitkumarbhamu.easycut.R;

public class EditInfoDialog extends AppCompatDialogFragment {

    private EditText barberName,barberaddress,barberOpeningTime;
    private ImageView barberImage;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_info_dialog,null);//this is the view that gonna shwo on dialog

        builder.setView(view)
                .setTitle("Update Info")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        barberName=view.findViewById(R.id.editedBarberName);
        barberOpeningTime=view.findViewById(R.id.editedBarberOpeningTime);
        barberaddress=view.findViewById(R.id.editedBarberaddress);
        barberImage=view.findViewById(R.id.editedbarberImage);


        return builder.create();


    }
}
