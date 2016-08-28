package com.jfireframework.mvc.util;

import java.io.File;

public class FileChangeDetect
{
    private final File root;
    private long       lastModitySum = 0;
    
    public FileChangeDetect(File root)
    {
        this.root = root;
        lastModitySum = calculateModitySum(root);
    }
    
    private long calculateModitySum(File file)
    {
        if (file.isDirectory())
        {
            long sum = 0;
            for (File each : file.listFiles())
            {
                sum += calculateModitySum(each);
            }
            return sum;
        }
        else
        {
            return file.lastModified();
        }
    }
    
    public boolean detectChange()
    {
        long newModitySum = calculateModitySum(root);
        if (newModitySum == lastModitySum)
        {
            return false;
        }
        else
        {
            lastModitySum = newModitySum;
            return true;
        }
    }
    
}
