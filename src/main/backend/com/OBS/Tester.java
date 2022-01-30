package com.OBS;

import com.OBS.entity.CyclicalTransfer;
import com.OBS.lab.CyclicalTransferIterator;


import java.util.LinkedList;
import java.util.List;

public class Tester {
    public static void main(String[] args) {
        String category = "Rachunki";
        String title = "Gaz";
        float amount = 190.00f;

        CyclicalTransfer cyclicalTransfer = new CyclicalTransfer();
        cyclicalTransfer.setAmount(amount);
        cyclicalTransfer.setCategory(category);
        cyclicalTransfer.setTitle(title);

        List<CyclicalTransfer> cyclicalTransfers = new LinkedList<>();
        cyclicalTransfers.add(cyclicalTransfer);

        CyclicalTransferIterator iterator = new CyclicalTransferIterator(cyclicalTransfers);

        try {
            while (iterator.hasNext()) {
                if (iterator.equals(cyclicalTransfer)) {
                    throw (new Exception("Found exact cyclical transfer"));
                }
                iterator.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
