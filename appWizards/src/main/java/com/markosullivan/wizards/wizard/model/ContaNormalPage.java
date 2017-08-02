package com.markosullivan.wizards.wizard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ronan.lima on 01/08/17.
 */

public class ContaNormalPage extends MultipleFixedChoicePage {
    private List<Branch> mBranches = new ArrayList<>();

    public ContaNormalPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    private static class Branch {
        public String choice;
        public PageList childPageList;

        private Branch(String choice, PageList childPageList) {
            this.choice = choice;
            this.childPageList = childPageList;
        }
    }

    public ContaNormalPage addBranch(String choice, Page... childPages) {
        PageList childPageList = new PageList(childPages);
        for (Page page : childPageList) {
            page.setParentKey(choice);
        }
        mBranches.add(new Branch(choice, childPageList));
        return this;
    }
}
