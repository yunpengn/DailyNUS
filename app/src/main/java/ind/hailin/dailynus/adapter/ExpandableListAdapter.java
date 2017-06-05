package ind.hailin.dailynus.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.entity.Groups;

/**
 * Created by hailin on 2017/6/5.
 * This is an adapter for ExpandableListView
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    public static final String TAG = "ExpandableListAdapter";

    private Context context;

    private List<String> groupList;
    private Map<String, List<Groups>> childMap;

    public ExpandableListAdapter(Context context, List<String> groupList, Map<String, List<Groups>> childMap) {
        this.context = context;
        this.groupList = groupList;
        this.childMap = childMap;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childMap.get(groupList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (getChildrenCount(groupPosition) > 0)
            return childMap.get(groupList.get(groupPosition)).get(childPosition); /*return a Groups object*/
        else
            return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_expandable_group, null);
        }
        String groupname = (String) getGroup(groupPosition);
        TextView tvName = (TextView) convertView.findViewById(R.id.item_group_tv_groupname);
        TextView tvAdd = (TextView) convertView.findViewById(R.id.item_group_tv_add);

        tvName.setText(groupname);
        if(groupPosition != 0){
            tvAdd.setVisibility(View.VISIBLE);
            tvAdd.setEnabled(true);
            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO jump to search page
                }
            });
        } else {
            tvAdd.setVisibility(View.INVISIBLE);
            tvAdd.setEnabled(false);
            tvAdd.setClickable(false);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_expandable_item, null);
        }
        Groups group = ((Groups) getChild(groupPosition, childPosition));
        if(group != null) {
            String itemName = group.getName();
            TextView tvChild = (TextView) convertView.findViewById(R.id.item_item_textview);
            tvChild.setText(itemName);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updateChildMap(Map<String, List<Groups>> map) {
        this.childMap = map;
        notifyDataSetChanged();
    }
}
