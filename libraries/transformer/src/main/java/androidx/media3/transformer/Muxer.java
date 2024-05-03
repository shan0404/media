/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.media3.transformer;

import android.media.MediaCodec.BufferInfo;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.Metadata;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.muxer.Muxer.TrackToken;
import com.google.common.collect.ImmutableList;
import java.nio.ByteBuffer;

/**
 * Abstracts media muxing operations.
 *
 * <p>Query whether {@linkplain Factory#getSupportedSampleMimeTypes(int) sample MIME types} are
 * supported and {@linkplain #addTrack(Format) add all tracks}, then {@linkplain #writeSampleData
 * write sample data} to mux samples. Once any sample data has been written, it is not possible to
 * add tracks. After writing all sample data, {@linkplain #release() release} the instance to finish
 * writing to the output and return any resources to the system.
 */
@UnstableApi
// TODO: b/330695864 - Replace with the Muxer interface from the Muxer module.
public interface Muxer {

  /** Thrown when a muxing failure occurs. */
  final class MuxerException extends Exception {
    /**
     * Creates an instance.
     *
     * @param message See {@link #getMessage()}.
     * @param cause See {@link #getCause()}.
     */
    public MuxerException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  /** Factory for muxers. */
  interface Factory {
    /**
     * Returns a new muxer writing to a file.
     *
     * @param path The path to the output file.
     * @throws IllegalArgumentException If the path is invalid.
     * @throws MuxerException If an error occurs opening the output file for writing.
     */
    Muxer create(String path) throws MuxerException;

    /**
     * Returns the supported sample {@linkplain MimeTypes MIME types} for the given {@link
     * C.TrackType}.
     */
    ImmutableList<String> getSupportedSampleMimeTypes(@C.TrackType int trackType);
  }

  /**
   * Adds a track with the specified format.
   *
   * @param format The {@link Format} of the track.
   * @return The {@link TrackToken} for this track, which should be passed to {@link
   *     #writeSampleData}.
   * @throws MuxerException If the muxer encounters a problem while adding the track.
   */
  TrackToken addTrack(Format format) throws MuxerException;

  /**
   * Writes the specified sample.
   *
   * @param trackToken The {@link TrackToken} of the track, previously returned by {@link
   *     #addTrack(Format)}.
   * @param data A buffer containing the sample data to write to the container.
   * @param bufferInfo The {@link BufferInfo} of the sample.
   * @throws MuxerException If the muxer fails to write the sample.
   */
  void writeSampleData(TrackToken trackToken, ByteBuffer data, BufferInfo bufferInfo)
      throws MuxerException;

  /** Adds {@linkplain Metadata.Entry metadata} about the output file. */
  void addMetadataEntry(Metadata.Entry metadataEntry);

  /**
   * Finishes writing the output and releases any resources associated with muxing.
   *
   * <p>The muxer cannot be used anymore once this method has been called.
   *
   * @throws MuxerException If the muxer fails to finish writing the output and {@code
   *     forCancellation} is false.
   */
  void release() throws MuxerException;
}
