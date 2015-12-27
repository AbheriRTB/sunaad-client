package com.abheri.sunaad.view;

/**
 * Created by Maha on 27/12/15.
 */

        import java.util.List;
        import java.util.Map;

        //import com.theopentutorials.expandablelist.R;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.graphics.Typeface;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.ViewGroup;
        import android.widget.BaseExpandableListAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.abheri.sunaad.R;
        import com.abheri.sunaad.dao.Program;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> artisteCollections;
    private List<String> artistes;

    public ExpandableListAdapter(Activity context, List<String> artList,
                                 Map<String, List<String>> artCollections) {
        this.context = context;
        this.artisteCollections = artCollections;
        this.artistes = artList;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return artisteCollections.get(artistes.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Program program = (Program) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.artiste_child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.programDetail);

        /*
        ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                List<String> child =
                                        artisteCollections.get(artistes.get(groupPosition));
                                child.remove(childPosition);
                                notifyDataSetChanged();
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        */

        item.setText(program.getDetails());
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return artisteCollections.get(artistes.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return artistes.get(groupPosition);
    }

    public int getGroupCount() {
        return artistes.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String laptopName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.artiste_group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.artiste);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(laptopName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}