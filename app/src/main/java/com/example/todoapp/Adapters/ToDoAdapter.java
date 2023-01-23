package com.example.todoapp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Models.ToDoModel;
import com.example.todoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoHolder> {
    private ArrayList<ToDoModel> models;
    private Context context;
    private String timePeriod;
    private Activity activity;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/");

    public ToDoAdapter(ArrayList<ToDoModel> models, Context context, String timePeriod, Activity activity) {
        this.models = models;
        this.context = context;
        this.timePeriod = timePeriod;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ToDoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_to_do, parent, false);
        return new ToDoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapter.ToDoHolder holder, int position) {
        holder.toDoCheckBox.setChecked(models.get(position).isSelected());
        holder.toDoCheckBox.setText(models.get(position).getContent());
        holder.toDoCheckBox.setPaintFlags(models.get(position).isSelected() ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
        holder.toDoCheckBox.setTypeface(holder.toDoCheckBox.getTypeface(), models.get(position).isSelected() ? Typeface.BOLD_ITALIC : Typeface.BOLD);
   }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ToDoHolder extends RecyclerView.ViewHolder {
        CheckBox toDoCheckBox;
        ImageView toDoPopup;
        public ToDoHolder(@NonNull View itemView) {
            super(itemView);
            toDoCheckBox = itemView.findViewById(R.id.toDoCheckBox);
            toDoPopup = itemView.findViewById(R.id.toDoPopup);

            toDoCheckBox.setOnClickListener(view -> {
                ToDoModel model = models.get(getAdapterPosition());
                model.setSelected(!model.isSelected());

                assert FirebaseAuth.getInstance().getUid() !=null;
                DatabaseReference reference = firebaseDatabase.getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid()).child(timePeriod);

                reference.child(model.getId()).setValue(model);
                toDoCheckBox.setChecked(model.isSelected());

                toDoCheckBox.setPaintFlags(model.isSelected() ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
                toDoCheckBox.setTypeface(toDoCheckBox.getTypeface(), model.isSelected() ? Typeface.BOLD_ITALIC : Typeface.BOLD);
                notifyDataSetChanged();
            });

            toDoPopup.setOnClickListener(view -> {
                PopupMenu popupMenu =new PopupMenu(context.getApplicationContext(), toDoPopup);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.todo_popup_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.toDoPopupDelete){
                        deleteToDo();
                    }else if (menuItem.getItemId() == R.id.toDoPopupEdit){
                        updateToDo();
                    }
                    return false;
                });
            });

        }

        private void deleteToDo() {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.deleteToDoTitle);
            builder.setMessage(R.string.deleteToDoMessage);
            builder.setPositiveButton(R.string.deleteToDoPossitiveMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Task<Void> delete = firebaseDatabase
                            .getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid())
                            .child(timePeriod)
                            .child(models.get(getAdapterPosition()).getId()).removeValue();
                    delete.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context.getApplicationContext(), "Deleted.", Toast.LENGTH_SHORT).show();
                                // models.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }else
                                Toast.makeText(context.getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builder.setNegativeButton(R.string.toDoMessageNegativeMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void updateToDo() {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.edit_form);
            EditText editTxtFormToDo = dialog.findViewById(R.id.editTxtFormToDo);
            editTxtFormToDo.setText(toDoCheckBox.getText());
            Button btnUpdateToDo = dialog.findViewById(R.id.btnUpdateToDo);
            Button btnCancelToDo = dialog.findViewById(R.id.btnCancelToDo);
            if (editTxtFormToDo.getText() != null){
                btnUpdateToDo.setOnClickListener(view -> {
                    ToDoModel model = models.get(getAdapterPosition());
                    model.setContent(editTxtFormToDo.getText().toString());
                    if (model.getId() != null){
                        Task<Void> performUpdate = firebaseDatabase.getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid()).child(timePeriod)
                                .child(models.get(getAdapterPosition()).getId()).setValue(model);
                        performUpdate.addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context.getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    notifyDataSetChanged();
                                }else
                                    Toast.makeText(context.getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(context.getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            btnCancelToDo.setOnClickListener(view1 -> dialog.dismiss());
            dialog.show();
        }
    }
}
