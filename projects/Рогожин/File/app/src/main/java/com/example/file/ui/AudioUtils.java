package com.example.file.ui;

import android.content.Context;
import android.media.*;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class AudioUtils {

    /**
     * Конвертирует аудио в WAV: 16kHz mono PCM16
     */
    public static void convertTo16kMono(Uri inputUri, File outputFile, Context context) throws Exception {

        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(context, inputUri, null);

        int trackIndex = -1;
        MediaFormat format = null;

        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat f = extractor.getTrackFormat(i);
            String mime = f.getString(MediaFormat.KEY_MIME);
            if (mime != null && mime.startsWith("audio/")) {
                trackIndex = i;
                format = f;
                break;
            }
        }

        if (trackIndex < 0) throw new RuntimeException("No audio track");

        extractor.selectTrack(trackIndex);

        int inputSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

        String mime = format.getString(MediaFormat.KEY_MIME);
        MediaCodec codec = MediaCodec.createDecoderByType(mime);
        codec.configure(format, null, null, 0);
        codec.start();

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        FileOutputStream fos = new FileOutputStream(outputFile);
        writeWavHeader(fos, 1, 16000, 0);

        boolean isEOS = false;
        int totalBytes = 0;

        while (true) {

            // ===== INPUT =====
            if (!isEOS) {
                int inIndex = codec.dequeueInputBuffer(10000);
                if (inIndex >= 0) {
                    ByteBuffer buffer = codec.getInputBuffer(inIndex);
                    int sampleSize = extractor.readSampleData(buffer, 0);

                    if (sampleSize < 0) {
                        codec.queueInputBuffer(inIndex, 0, 0, 0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isEOS = true;
                    } else {
                        long time = extractor.getSampleTime();
                        codec.queueInputBuffer(inIndex, 0, sampleSize, time, 0);
                        extractor.advance();
                    }
                }
            }

            // ===== OUTPUT =====
            int outIndex = codec.dequeueOutputBuffer(info, 10000);
            if (outIndex >= 0) {
                ByteBuffer outBuffer = codec.getOutputBuffer(outIndex);

                byte[] chunk = new byte[info.size];
                outBuffer.get(chunk);
                outBuffer.clear();

                // PCM16 → short[]
                short[] samples = bytesToShorts(chunk);

                // stereo → mono
                short[] mono = (channelCount == 2)
                        ? stereoToMono(samples)
                        : samples;

                // нормальный ресэмплинг
                short[] resampled = resampleLinear(mono, inputSampleRate, 16000);

                byte[] outBytes = shortsToBytes(resampled);
                fos.write(outBytes);

                totalBytes += outBytes.length;

                codec.releaseOutputBuffer(outIndex, false);

                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            }
        }

        // переписать WAV header
        fos.flush();
        fos.getChannel().position(0);
        writeWavHeader(fos, 1, 16000, totalBytes);

        fos.close();
        codec.stop();
        codec.release();
        extractor.release();
    }

    // =========================
    // 🎧 AUDIO HELPERS
    // =========================

    private static short[] bytesToShorts(byte[] data) {
        short[] out = new short[data.length / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] << 8));
        }
        return out;
    }

    private static byte[] shortsToBytes(short[] data) {
        byte[] out = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            out[i * 2] = (byte) (data[i] & 0xff);
            out[i * 2 + 1] = (byte) ((data[i] >> 8) & 0xff);
        }
        return out;
    }

    private static short[] stereoToMono(short[] stereo) {
        short[] mono = new short[stereo.length / 2];

        for (int i = 0, j = 0; i < stereo.length; i += 2, j++) {
            int left = stereo[i];
            int right = stereo[i + 1];
            mono[j] = (short) ((left + right) / 2);
        }

        return mono;
    }

    /**
     * 🔥 НОРМАЛЬНЫЙ RESAMPLER (linear interpolation)
     */
    private static short[] resampleLinear(short[] input, int inRate, int outRate) {

        if (inRate == outRate) return input;

        float ratio = (float) inRate / outRate;
        int outLength = (int) (input.length / ratio);

        short[] output = new short[outLength];

        for (int i = 0; i < outLength; i++) {

            float index = i * ratio;
            int i0 = (int) index;
            int i1 = Math.min(i0 + 1, input.length - 1);

            float frac = index - i0;

            float sample = (1 - frac) * input[i0] + frac * input[i1];

            output[i] = (short) sample;
        }

        return output;
    }

    // =========================
    // 🧾 WAV HEADER
    // =========================

    private static void writeWavHeader(FileOutputStream out,
                                       int channels,
                                       int sampleRate,
                                       int dataLength) throws Exception {

        int byteRate = sampleRate * channels * 2;
        byte[] header = new byte[44];

        // RIFF
        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        int fileSize = 36 + dataLength;
        writeInt(header, 4, fileSize);

        // WAVE
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';

        // fmt
        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
        writeInt(header, 16, 16);
        writeShort(header, 20, (short) 1);
        writeShort(header, 22, (short) channels);
        writeInt(header, 24, sampleRate);
        writeInt(header, 28, byteRate);
        writeShort(header, 32, (short) (channels * 2));
        writeShort(header, 34, (short) 16);

        // data
        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
        writeInt(header, 40, dataLength);

        out.write(header, 0, 44);
    }

    private static void writeInt(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xff);
        data[offset + 1] = (byte) ((value >> 8) & 0xff);
        data[offset + 2] = (byte) ((value >> 16) & 0xff);
        data[offset + 3] = (byte) ((value >> 24) & 0xff);
    }

    private static void writeShort(byte[] data, int offset, short value) {
        data[offset] = (byte) (value & 0xff);
        data[offset + 1] = (byte) ((value >> 8) & 0xff);
    }
}