package com.markosullivan.wizards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.ModelCallbacks;
import com.markosullivan.wizards.wizard.model.Page;
import com.markosullivan.wizards.wizard.model.ReviewItem;
import com.markosullivan.wizards.wizard.ui.PageFragmentCallbacks;
import com.markosullivan.wizards.wizard.ui.ReviewFragment;
import com.markosullivan.wizards.wizard.ui.StepPagerStrip;

import java.util.List;

public class MainActivityWizard extends FragmentActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private AbstractWizardModel mWizardModel;// = new WizardContaNormal(this);
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private List<ReviewItem> itensSelecteds;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wizard);

        mWizardModel = new WizardNotaServico(this);
        //FIXME descomentar bloco abaixo
//        if (getIntent().getStringExtra(getResources().getString(R.string.dados_qr_code)).split(";")[0].equals(getResources().getString(R.string.tipo_conta_normal))) {
//            mWizardModel = new WizardContaNormal(this);
//        }

        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    if (getItensSelecteds() != null && getItensSelecteds().size() > 0) {
                        boolean isContaProcotolada = false;
                        boolean isContaColetiva = false;
                        if (getItensSelecteds().get(1) != null && getItensSelecteds().get(1).getDisplayValue().contains(WizardContaNormal.choicesSobreConta[0])) {
                            isContaProcotolada = true;
                        }
                        if (getItensSelecteds().get(1) != null && getItensSelecteds().get(1).getDisplayValue().contains(WizardContaNormal.choicesSobreConta[1])) {
                            isContaColetiva = true;
                        }
                        Bundle b = new Bundle();
                        b.putBoolean("contaProtocolada", isContaProcotolada);
                        b.putBoolean("contaColetiva", isContaColetiva);
                        b.putString("localEntrega", getItensSelecteds().get(0).getDisplayValue());
                        Intent i = new Intent();
                        i.putExtras(b);
                        setResult(Activity.RESULT_OK, i);
//                        listenerCallback.backToMainApplication(b);
                    }
                    finish();
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED, new Intent());
    }

    /**
     * Listener utilizado para recuperar os itens marcados no passo onde o usuário informa se a conta
     * está protocolada e/ou é coletiva
     *
     * @return
     */
    private ReviewFragment.ListenerConta retrieveListenerConta() {
        return new ReviewFragment.ListenerConta() {
            @Override
            public void getInfoAboutConta(List<ReviewItem> itensSelecteds) {
                setItensSelecteds(itensSelecteds);
            }
        };
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    public List<ReviewItem> getItensSelecteds() {
        return itensSelecteds;
    }

    public void setItensSelecteds(List<ReviewItem> itensSelecteds) {
        this.itensSelecteds = itensSelecteds;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment(retrieveListenerConta());
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}
