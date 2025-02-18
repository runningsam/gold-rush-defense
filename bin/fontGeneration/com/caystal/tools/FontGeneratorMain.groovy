package com.caystal.tools

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.tools.bmfont.BitmapFontWriter
import com.badlogic.gdx.tools.bmfont.BitmapFontWriter.FontInfo

class FontGeneratorMain {
    static void main(String[] args) {
        def config = new Lwjgl3ApplicationConfiguration()
        config.setWindowedMode(1, 1)
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4)
        
        new Lwjgl3Application(new ApplicationAdapter() {
            @Override
            void create() {
                try {
                    FreeTypeFontParameter param = new FreeTypeFontParameter()
                    param.size = 32
                    param.gamma = 2f
                    param.shadowOffsetY = 1
                    param.renderCount = 3
                    param.shadowColor = new Color(0, 0, 0, 0.45f)
                    param.characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?~/"\'\\'
                    param.packer = new PixmapPacker(512, 512, Format.RGBA8888, 2, false)

                    FileHandle fontFile = Gdx.files.internal("assets/uiskin/Arial.ttf")
                    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile)
                    def fontData = generator.generateData(param)
                    
                    // 创建 FontInfo
                    def info = new FontInfo()
                    info.padding = new BitmapFontWriter.Padding(1, 1, 1, 1)
                    
                    // 保存字体文件
                    BitmapFontWriter.writeFont(
                        fontData,
                        ["default.png"] as String[],
                        Gdx.files.local("assets/uiskin/default.fnt"),
                        info,
                        512,
                        512
                    )
                    
                    // 保存图片
                    BitmapFontWriter.writePixmaps(
                        param.packer.getPages(),
                        Gdx.files.local("assets/uiskin"),
                        "default"
                    )
                    
                    generator.dispose()
                    param.packer.dispose()
                } finally {
                    Gdx.app.exit()
                }
            }
        }, config)
    }
} 