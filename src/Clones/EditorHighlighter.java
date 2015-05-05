package Clones;

import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.maxgarfinkel.suffixTree.CloneClass;
import com.maxgarfinkel.suffixTree.TokenRange;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Created by llama on 14.04.15.
 */
@Deprecated
public class EditorHighlighter {
    final MarkupModel markupModel;
//    final TextAttributes textAttributes = new TextAttributes(null, Color.lightGray,null,null,0);
    private final Random random = new Random();

    public EditorHighlighter(@NotNull final MarkupModel markupModel) {
        this.markupModel = markupModel;
    }

    public void highlight(List<CloneClass> clones){
        markupModel.removeAllHighlighters();
        for (CloneClass cloneClass : clones){
            TextAttributes attributes = getRandomAttributes();
            for (TokenRange range : cloneClass.getClones()) {
                markupModel.addRangeHighlighter(range.begin.source.getTextOffset(), range.end.source.getTextOffset() + range.end.source.getTextLength(), 1, attributes, HighlighterTargetArea.EXACT_RANGE);
            }
        }
    }

    @NotNull
    private TextAttributes getRandomAttributes(){
        Color color = Color.getHSBColor(
                random.nextFloat(),
                random.nextFloat(),
                random.nextFloat()
        );
        return new TextAttributes(null, color, null, null,0);
    }
}
