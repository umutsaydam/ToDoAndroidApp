package com.example.todoapp.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Models.ToDoModel;
import com.example.todoapp.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoHolder> {
    private final ArrayList<ToDoModel> models;
    private final Context context;
    private final String timePeriod;
    private final Activity activity;
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
        ToDoModel model = models.get(position);
        holder.toDoCheckBox.setChecked(model.isSelected());
        holder.toDoCheckBox.setText(model.getContent());
        holder.toDoCheckBox.setPaintFlags(model.isSelected() ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
        holder.toDoCheckBox.setTypeface(holder.toDoCheckBox.getTypeface(), model.isSelected() ? Typeface.BOLD_ITALIC : Typeface.BOLD);
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
                DatabaseReference reference = firebaseDatabase.getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid()+"/ToDo/").child(timePeriod);

                reference.child(model.getId()).setValue(model);
                toDoCheckBox.setChecked(model.isSelected());

                toDoCheckBox.setPaintFlags(model.isSelected() ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
                toDoCheckBox.setTypeface(toDoCheckBox.getTypeface(), model.isSelected() ? Typeface.BOLD_ITALIC : Typeface.BOLD);
                notifyDataSetChanged();
            });

            toDoPopup.setOnClickListener(view -> {
                PopupMenu popupMenu =new PopupMenu(context, toDoPopup);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialogStyle);
            builder.setTitle(R.string.deleteToDoTitle);
            builder.setMessage(R.string.deleteToDoMessage);
            builder.setPositiveButton(R.string.deleteEventPossitiveMessage, (dialogInterface, i) -> {
                Task<Void> delete = firebaseDatabase
                        .getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid()+"/ToDo/")
                        .child(timePeriod)
                        .child(models.get(getAdapterPosition()).getId()).removeValue();
                delete.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context.getApplicationContext(), "Deleted.", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }else
                        Toast.makeText(context.getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                });
            });
            builder.setNegativeButton(R.string.toDoMessageNegativeMessage, (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void updateToDo() {
            Dialog dialog = new Dialog(activity, R.style.dialogStyle);
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
                        Task<Void> performUpdate = firebaseDatabase.getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid()+"/ToDo/").child(timePeriod)
                                .child(models.get(getAdapterPosition()).getId()).setValue(model);
                        performUpdate.addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Toast.makeText(context.getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                notifyDataSetChanged();
                            }else
                                Toast.makeText(context.getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
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
