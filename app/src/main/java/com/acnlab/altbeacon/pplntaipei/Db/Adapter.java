package com.acnlab.altbeacon.pplntaipei.Db;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.util.List;

import com.acnlab.altbeacon.pplntaipei.ActionKirimActivity;
import com.acnlab.altbeacon.pplntaipei.ListAllActivity;
import com.acnlab.altbeacon.pplntaipei.ListKirimActivity;
import com.acnlab.altbeacon.pplntaipei.ListTerimaActivity;
import com.acnlab.altbeacon.pplntaipei.MainActivity;
import com.acnlab.altbeacon.pplntaipei.R;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder>{

    private Context context;
    private List<Data> List;
    private DatabaseHelper databaseHelper;
    private int jenisAdapter;
    public final static int DATA_KIRIM=1;
    public final static int DATA_TERIMA=2;
    public final static int DATA_SEMUA=3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView data;
        ImageView delete,edit;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            data = view.findViewById(R.id.note);
            timestamp = view.findViewById(R.id.timestamp);
            delete = view.findViewById(R.id.delete);
            edit = view.findViewById(R.id.edit);
        }
    }


    public Adapter(Context context, List<Data> List, DatabaseHelper dbhelper, int jenisAdapter){
        this.jenisAdapter=jenisAdapter;
        this.context = context;
        this.List = List;
        this.databaseHelper = dbhelper;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_content, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder,final int position) {
        Data diary = List.get(position);
        String dataUser="";
        dataUser=diary.getDataUser();
        holder.data.setText(diary.getUrutan()+": "+diary.getDataUser());

        // Formatting and displaying timestamp
        holder.timestamp.setText("kode tracking: "+diary.getDataPos());
        final String finalDataUser = dataUser;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi menghapus")
                        .setMessage("Anda yakin ingin menghapus data "+ finalDataUser +" ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteNote(position);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d("MainActivity", "Aborting mission...");
                            }
                        })
                        .show();

            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
                //updateNote("test",position);
                promptDialogEdit(position);

            }
        });
    }

    public int getItemCount() {
        return List.size();
    }

    private void promptDialogEdit(int position) {

        final Data dataUbah=List.get(position);

        final EditText edtTextUser = new EditText(context);
        final EditText edtTextPos = new EditText(context);

        LinearLayout layout=new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,0,10,0);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        edtTextUser.setText(dataUbah.getDataUser());
        edtTextPos.setText(dataUbah.getDataPos());

        layout.addView(edtTextUser,params);
        layout.addView(edtTextPos,params);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ubah data");
        builder.setCancelable(false);
        builder.setView(layout);

        builder.setNeutralButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataUbah.setDataPos(edtTextPos.getText().toString());
                dataUbah.setDataUser(edtTextUser.getText().toString());
                databaseHelper.updateData(dataUbah);

                if(jenisAdapter==DATA_KIRIM){
                    ListKirimActivity.notifyAdapter();
                }else if(jenisAdapter==DATA_TERIMA){
                    ListTerimaActivity.notifyAdapter();
                }else{
                    ListAllActivity.notifyAdapter();
                }
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void deleteNote(int position) {
        // deleting the data from db
        databaseHelper.deleteData(List.get(position));

        // removing the data from the list
        List.remove(position);
        if(jenisAdapter==DATA_KIRIM){
            ListKirimActivity.notifyAdapter();
        }else if(jenisAdapter==DATA_TERIMA){
            ListTerimaActivity.notifyAdapter();
        }else{
            ListAllActivity.notifyAdapter();
        }

    }

}
