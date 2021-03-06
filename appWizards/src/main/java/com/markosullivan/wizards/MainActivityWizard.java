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
import android.widget.Toast;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.ModelCallbacks;
import com.markosullivan.wizards.wizard.model.Page;
import com.markosullivan.wizards.wizard.ui.PageFragmentCallbacks;
import com.markosullivan.wizards.wizard.ui.ReviewFragment;
import com.markosullivan.wizards.wizard.ui.StepPagerStrip;

import java.util.List;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_DEVE_TIRAR_FOTO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_TIPO_CONTA;

public class MainActivityWizard extends FragmentActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private AbstractWizardModel mWizardModel;
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mPhotoButton;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private String tipoConta;
    private Intent mIntent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wizard);

        mIntent = getIntent();
        tipoConta = mIntent.getStringExtra(EXTRA_TIPO_CONTA);

        if (tipoConta.equals(getResources().getString(R.string.tipo_conta_normal))) {
//            mWizardModel = new WizardContaNormal(this, true);
            mWizardModel = new SingleWizard(this, true);
        } else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))
                || tipoConta.equals(getResources().getString(R.string.tipo_conta_desligamento))) {
            mWizardModel = new WizardContaNormal(this, false);
        } else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_nota))) {
            mWizardModel = new WizardNotaServico(this);
        } else {
            mWizardModel = new WizardNoQrCode(this);
        }

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
        mPhotoButton = (Button) findViewById(R.id.photo_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size() - 1) {
                    if (mCurrentPageSequence.get(mPager.getCurrentItem()).isRequired() && mCurrentPageSequence.get(mPager.getCurrentItem()).isCompleted()) {
                        finishWizard(false);
                    } else if (!mCurrentPageSequence.get(mPager.getCurrentItem()).isRequired()) {
                        finishWizard(false);
                    } else {
                        Toast.makeText(getBaseContext(), "É necessário responder o questionário antes de continuar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
            }
        });

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size() - 1) {
                    if (mCurrentPageSequence.get(mPager.getCurrentItem()).isCompleted()) {
                        finishWizard(true);
                    } else if (!mCurrentPageSequence.get(mPager.getCurrentItem()).isRequired()) {
                        finishWizard(true);
                    } else {
                        Toast.makeText(getBaseContext(), "É necessário responder o questionário antes de continuar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
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
//    FIXME *MEMORIZACAO_WIZARD* foi solicitado que o sistema grave a última resposta do wizard, no entanto, como é algo mais trabalhoso, acordar um prazo e/ou valor para que seja feito.
//        mStepPagerStrip.getPageSelectedListener().onPageStripSelected(mWizardModel.getSizePageList());
    }

    private void finishWizard(boolean value) {
        mIntent.putExtras(mWizardModel.getBundleOfPages(mIntent.getExtras()));
        mIntent.putExtra(EXTRA_DEVE_TIRAR_FOTO, value);
        setResult(Activity.RESULT_OK, mIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED, new Intent());
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size()); // + 1 = review step
//        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size() - 1) {
            mNextButton.setText(R.string.finish);
            if (!tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))) {
                mPhotoButton.setVisibility(View.VISIBLE);
            }
        } else {
            mPhotoButton.setVisibility(View.GONE);
            mNextButton.setText(R.string.next);
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
        int cutOffPage = mCurrentPageSequence.size() - 1;
//        int cutOffPage = mCurrentPageSequence.size();
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

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
//            if (i >= mCurrentPageSequence.size()) {
//                return new ReviewFragment();
//            }

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
//            return mCurrentPageSequence.size();
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
