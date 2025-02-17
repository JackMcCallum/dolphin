// SPDX-License-Identifier: GPL-2.0-or-later

package org.dolphinemu.dolphinemu.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.dolphinemu.dolphinemu.NativeLibrary;
import org.dolphinemu.dolphinemu.R;
import org.dolphinemu.dolphinemu.databinding.DialogGameDetailsBinding;
import org.dolphinemu.dolphinemu.model.GameFile;
import org.dolphinemu.dolphinemu.services.GameFileCacheManager;
import org.dolphinemu.dolphinemu.utils.GlideUtils;

public final class GameDetailsDialog extends DialogFragment
{
  private static final String ARG_GAME_PATH = "game_path";

  public static GameDetailsDialog newInstance(String gamePath)
  {
    GameDetailsDialog fragment = new GameDetailsDialog();

    Bundle arguments = new Bundle();
    arguments.putString(ARG_GAME_PATH, gamePath);
    fragment.setArguments(arguments);

    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    GameFile gameFile = GameFileCacheManager.addOrGet(getArguments().getString(ARG_GAME_PATH));

    DialogGameDetailsBinding binding = DialogGameDetailsBinding.inflate(getLayoutInflater());

    String country = getResources().getStringArray(R.array.countryNames)[gameFile.getCountry()];
    String description = gameFile.getDescription();
    String fileSize = NativeLibrary.FormatSize(gameFile.getFileSize(), 2);

    binding.textGameTitle.setText(gameFile.getTitle());
    binding.textDescription.setText(gameFile.getDescription());
    if (description.isEmpty())
    {
      binding.textDescription.setVisibility(View.GONE);
    }

    binding.textCountry.setText(country);
    binding.textCompany.setText(gameFile.getCompany());
    binding.textGameId.setText(gameFile.getGameId());
    binding.textRevision.setText(String.valueOf(gameFile.getRevision()));

    if (!gameFile.shouldShowFileFormatDetails())
    {
      binding.labelFileFormat.setText(R.string.game_details_file_size);
      binding.textFileFormat.setText(fileSize);

      binding.labelCompression.setVisibility(View.GONE);
      binding.textCompression.setVisibility(View.GONE);
      binding.labelBlockSize.setVisibility(View.GONE);
      binding.textBlockSize.setVisibility(View.GONE);
    }
    else
    {
      long blockSize = gameFile.getBlockSize();
      String compression = gameFile.getCompressionMethod();

      binding.textFileFormat.setText(getResources().getString(R.string.game_details_size_and_format,
              gameFile.getFileFormatName(), fileSize));

      if (compression.isEmpty())
      {
        binding.textCompression.setText(R.string.game_details_no_compression);
      }
      else
      {
        binding.textCompression.setText(gameFile.getCompressionMethod());
      }

      if (blockSize > 0)
      {
        binding.textBlockSize.setText(NativeLibrary.FormatSize(blockSize, 0));
      }
      else
      {
        binding.labelBlockSize.setVisibility(View.GONE);
        binding.textBlockSize.setVisibility(View.GONE);
      }
    }

    GlideUtils.loadGameBanner(binding.banner, gameFile);

    return new MaterialAlertDialogBuilder(requireActivity())
            .setView(binding.getRoot())
            .create();
  }
}
