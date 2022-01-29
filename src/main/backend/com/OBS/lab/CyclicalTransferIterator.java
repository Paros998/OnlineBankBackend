package com.OBS.lab;

import com.OBS.entity.CyclicalTransfer;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class CyclicalTransferIterator implements Iterator<CyclicalTransfer> {
    private final List<CyclicalTransfer> cyclicalTransfers;
    private CyclicalTransfer cyclicalTransfer;
    private boolean foundTransfer;
    private int index;

    public CyclicalTransferIterator(List<CyclicalTransfer> cyclicalTransfers) {
        this.cyclicalTransfers = cyclicalTransfers;
        index = -1;
        foundTransfer = false;
    }

    @Override
    public boolean hasNext() {
        for (foundTransfer = false, index++; index < cyclicalTransfers.size(); index++) {
            cyclicalTransfer = cyclicalTransfers.get(index);
            if (cyclicalTransfer != null) {
                foundTransfer = true;
                return true;
            }
        }
        return false;
    }

    public boolean equals(CyclicalTransfer newCyclicalTransfer) {
        if (!foundTransfer) return false;

        boolean hasSameAmount = Objects.equals(newCyclicalTransfer.getAmount(), cyclicalTransfer.getAmount());
        boolean hasSameReceiver = Objects.equals(newCyclicalTransfer.getReceiver(), cyclicalTransfer.getReceiver());
        boolean hasSameReTransferDate = Objects.equals(newCyclicalTransfer.getReTransferDate(), cyclicalTransfer.getReTransferDate());
        boolean hasSameCategory = Objects.equals(newCyclicalTransfer.getCategory(), cyclicalTransfer.getCategory());
        boolean hasSameAccountNumber = Objects.equals(newCyclicalTransfer.getAccountNumber(), cyclicalTransfer.getAccountNumber());
        boolean hasSameTitle = Objects.equals(newCyclicalTransfer.getTitle(), cyclicalTransfer.getTitle());

        return hasSameAmount && hasSameReceiver && hasSameReTransferDate && hasSameCategory && hasSameAccountNumber && hasSameTitle;
    }

    @Override
    public CyclicalTransfer next() {
        if (foundTransfer) return cyclicalTransfer;
        return null;
    }
}
