package br.com.home.maildeliveryjfsteel.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.fragment.RegistroContaNormalFragment;

public class RegistrosActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), this));

        int cor = getResources().getColor(R.color.branco);

        tabLayout.setTabTextColors(cor, cor);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.label_tipo_conta_normal)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.label_tipo_conta_nota_servico)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.label_tipo_conta_no_qrcode)));

        tabLayout.setOnTabSelectedListener(this);

        viewPager.addOnPageChangeListener(new TabLayoutListener(tabLayout));
        viewPager.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    class TabLayoutListener extends TabLayout.TabLayoutOnPageChangeListener {

        public TabLayoutListener(TabLayout tabLayout) {
            super(tabLayout);
        }
    }

    class TabsAdapter extends FragmentStatePagerAdapter {
        private RegistrosActivity activity;

        public TabsAdapter(FragmentManager fm, RegistrosActivity activity) {
            super(fm);
            this.activity = activity;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new RegistroContaNormalFragment(R.layout.fragment_registro_conta_normal);
            }
            return null; // TODO implementar criação dos outros 2 fragments
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
