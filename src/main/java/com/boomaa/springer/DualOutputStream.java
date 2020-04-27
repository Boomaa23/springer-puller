package com.boomaa.springer;

import java.io.*;

public class DualOutputStream extends OutputStream {
    private final PrintStream file = new PrintStream(new File(Main.LOG_FILE));
    private final OutputStream console = System.out;

    public DualOutputStream() throws FileNotFoundException {
    }

    @Override
    public void write(int b) throws IOException {
        file.write(b);
        console.write(b);
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        file.flush();
        console.flush();
    }

    @Override
    public void close() throws IOException {
        super.close();
        file.close();
    }
}
