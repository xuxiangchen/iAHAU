package net.peacesky.iahau.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.peacesky.iahau.R;

import java.util.List;

public class DrawerItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DrawerItem> drawerItems;

    private OnItemClickListener listener;

    public DrawerItemAdapter(List<DrawerItem> drawerItems) {
        this.drawerItems = drawerItems;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (DrawerItem.Type.values()[viewType]) {
            case HEADER:
                View headerRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
                return new HeaderViewHolder(headerRootView);
            case DIVIDER:
                View dividerRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_divider, parent, false);
                return new DividerViewHolder(dividerRootView);
            case MENU:
                View menuRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_menu, parent, false);
                return new MenuViewHolder(menuRootView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onClick(view, position);
            }
        });
        DrawerItem drawerItem = drawerItems.get(position);
        switch (drawerItem.getType()) {
            case MENU:
                MenuViewHolder menuViewHolder = (MenuViewHolder) holder;
                DrawerMenu drawerMenu = (DrawerMenu) drawerItem;
                menuViewHolder.itemTextView.setText(drawerMenu.getText());
                menuViewHolder.itemTextView.setCompoundDrawablesWithIntrinsicBounds(drawerMenu.getIconRes(), 0, 0, 0);
                break;
            case HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.itemTextView.setText(drawerItem.getText());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return drawerItems.get(position).getType().ordinal();
    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView itemTextView;

        public HeaderViewHolder(View rootView) {
            super(rootView);
            itemTextView = (TextView) rootView.findViewById(R.id.user_name);
        }
    }

    private static class DividerViewHolder extends RecyclerView.ViewHolder {

        public DividerViewHolder(View rootView) {
            super(rootView);
        }
    }

    private static class MenuViewHolder extends RecyclerView.ViewHolder {

        private TextView itemTextView;

        public MenuViewHolder(View rootView) {
            super(rootView);
            itemTextView = (TextView) rootView.findViewById(R.id.item);
        }
    }
}