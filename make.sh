#!/bin/bash

#//tb/1410

cur=`pwd`

src="$cur"/src
build="$cur"/build
archive="$cur"/archive
classes="$build"/classes
doc="$cur"/doc

package_path=ch/lowres/audio_rxtx/gui

windows_binaries_zip_name=audio_rxtx_1416837145.zip
windows_binaries_uri="https://raw.githubusercontent.com/7890/jack_tools/master/audio_rxtx/dist/win/$windows_binaries_zip_name"

#dl to (incl. filename)
windows_binaries_zip="/tmp/$windows_binaries_zip_name"

#splash_screen_image="$src"/gfx/audio_rxtx_splash_screen.png
#icon_image="$src"/gfx/audio_rxtx_icon.png

#-Xlint:all 

#needs tool check
#

#========================================================================
function create_build_info()
{
	now="`date`"
	uname="`uname -m -o`"
	jvm="`javac -version 2>&1 | head -1 | sed 's/"/''/g'`"
	javac_opts=" -source 1.6 -target 1.6"
	git_head_commit_id="`git rev-parse HEAD`"

	cat - << __EOF__
//generated at build time
package ch.lowres.audio_rxtx.gui.helpers;
import ch.lowres.audio_rxtx.gui.Main;
public class BuildInfo
{
	public static String get()
	{
		return "date: $now\nuname -m -o: $uname\njavac -version: $jvm\njavac "+Main.tr("Options")+": $javac_opts\ngit rev-parse HEAD: $git_head_commit_id";
	}
	public static String getGitCommit()
	{
		return "$git_head_commit_id";
	}
	public static void main(String[] args)
	{
		System.out.println(get());
	}
}
__EOF__
}

#========================================================================
function compile_audio_rxtx()
{
	echo "building audio_rxtx gui application"
	echo "==================================="

	mkdir -p "$classes"

	#apple extension are stubs used just at compile time on non-osx machines
	unzip -p "$archive"/AppleJavaExtensions.zip \
		AppleJavaExtensions/AppleJavaExtensions.jar > "$classes"/AppleJavaExtensions.jar

	javac -source 1.6 -target 1.6 -classpath "$classes":"$classes"/AppleJavaExtensions.jar -sourcepath "$src" -d "$classes" "$src"/$package_path/*.java

	ret=$?
	if [ $ret -ne 0 ]
	then
		echo "error while compiling."
		exit 1
	fi

	echo "start with:"
	echo "java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -cp .:build/classes/ ch.lowres.audio_rxtx.gui.Main"
}

#========================================================================
function compile_java_osc
{
	echo "building JavaOSC library (com.illposed.osc)"
	echo "==========================================="
	cp "$archive"/JavaOSC-master.zip "$build"
	cd "$build"
	unzip JavaOSC-master.zip
	cd "$cur"

	cp "$archive"/JavaOSC_mod/OSCPort.java "$build"/JavaOSC-master/modules/core/src/main/java/com/illposed/osc/
	cp "$archive"/JavaOSC_mod/OSCPortOut.java "$build"/JavaOSC-master/modules/core/src/main/java/com/illposed/osc/

	PREF="$build"/JavaOSC-master/modules/core/src/main/java

	echo "compiling files in $PREF to direcotry $classes ..."

	mkdir -p "$classes"
	javac -source 1.6 -target 1.6 -classpath $PREF -sourcepath $PREF -d "$classes" $PREF/com/illposed/osc/*.java
	javac -source 1.6 -target 1.6 -classpath $PREF -sourcepath $PREF -d "$classes" $PREF/com/illposed/osc/utility/*.java
	find "$classes"
}

#========================================================================
function compile_gettext
{
	echo "building gettext library (org.xnap.commons.i18n)"
	echo "================================================"
	cp "$archive"/gettext-commons-0.9.8-sources.jar "$build"
	cd "$build"
	jar xfv gettext-commons-0.9.8-sources.jar
	cd "$cur"

	PREF="$build"/

	echo "compiling files in $PREF to direcotry $classes ..."

	mkdir -p "$classes"
	javac -source 1.6 -target 1.6 -classpath $PREF -sourcepath $PREF -d "$classes" $PREF/org/xnap/commons/i18n/*.java
	find "$classes"
}

#========================================================================
function create_languages
{
	xgettext -ktrc:1c,2 -ktrnc:1c,2,3 -ktr -kmarktr -ktrn:1,2 \
		--from-code UTF-8 \
		-o "$build"/keys.pot \
		"$src"/$package_path/*.java \
		"$src"/$package_path/api/*.java \
		"$src"/$package_path/widgets/*.java \
		"$src"/$package_path/helpers/*.java \
		"$src"/$package_path/osc/*.java


	#en.po is header only
	cp "$src"/lang/en.po "$build"/en.po
	msgmerge -U "$build"/en.po "$build"/keys.pot
	#--no-fuzzy-matching

	#de
	cp "$src"/lang/de.po "$build"/de.po
	
	msgmerge -U "$build"/de.po "$build"/keys.pot

#	echo "edit $build/de.po with poedit? y or enter"
#	read a
	a=0

	if [ x"$a" = "xy" ]
	then
		echo "opening file $build/de.po..."
		poedit "$build"/de.po
		cp "$build"/de.po "$src"/lang/de.po		
	fi

	echo "creating i18n.Messages..."

	#source (en)
	msgfmt --java2 -d "$classes" -r ch.lowres.audio_rxtx.gui.i18n.Messages -l en "$build"/en.po

	#de
	msgfmt --java2 -d "$classes" -r ch.lowres.audio_rxtx.gui.i18n.Messages -l de "$build"/de.po

#detect errors / stop on error

}

#========================================================================
function build_jar
{
	echo "creating audio_rxtx application jar (audio_rxtx_gui_xxx.jar)"
	echo "============================================================"

	cur="`pwd`"

	mkdir -p "$classes"/resources/etc
	mkdir -p "$classes"/resources/fonts
	mkdir -p "$classes"/resources/images
	mkdir -p "$classes"/resources/licenses/ubuntu-font-family
	mkdir -p "$classes"/resources/licenses/JavaOSC
	mkdir -p "$classes"/resources/licenses/gettext-commons

	cp "$src"/gfx/audio_rxtx_icon.png "$classes"/resources/images
	cp "$src"/gfx/audio_rxtx_about_screen.png "$classes"/resources/images

	cp "$src"/gfx/arrow-up.png "$classes"/resources/images
	cp "$src"/gfx/arrow-left.png "$classes"/resources/images
	cp "$src"/gfx/arrow-right.png "$classes"/resources/images
	cp "$src"/gfx/arrow-down.png "$classes"/resources/images

	cp "$src"/etc/audio_rxtx_gui.properties "$classes"/resources/etc

	cp "$archive"/ubuntu-font-family-0.80.zip "$build"
	cd "$build"
	unzip ubuntu-font-family-0.80.zip
	cd "$cur"

	cp "$build"/ubuntu-font-family-0.80/Ubuntu-C.ttf "$classes"/resources/fonts/Ubuntu-C.ttf

	cp "$build"/ubuntu-font-family-0.80/LICENCE-FAQ.txt "$classes"/resources/licenses/ubuntu-font-family
	cp "$build"/ubuntu-font-family-0.80/copyright.txt "$classes"/resources/licenses/ubuntu-font-family
	cp "$build"/ubuntu-font-family-0.80/README.txt "$classes"/resources/licenses/ubuntu-font-family
	cp "$build"/ubuntu-font-family-0.80/TRADEMARKS.txt "$classes"/resources/licenses/ubuntu-font-family
	cp "$build"/ubuntu-font-family-0.80/LICENCE.txt "$classes"/resources/licenses/ubuntu-font-family

	cp "$build"/JavaOSC-master/LICENSE "$classes"/resources/licenses/JavaOSC

	cp "$archive"/gettext-commons-0.9.8-sources/* "$classes"/resources/licenses/gettext-commons

	##############################
	if [ ! -e "$windows_binaries_zip" ]
	then
		echo "curl -o $windows_binaries_zip $windows_binaries_uri"
		curl -o "$windows_binaries_zip" "$windows_binaries_uri"
	fi

	cp "$windows_binaries_zip" "$build"
	cd "$build"

	zip_filename="`echo \"$windows_binaries_zip\" | rev | cut -d\"/\" -f1 | rev`"
	unzip "$zip_filename"
	zip_dirname="`echo \"$zip_filename\" | rev | cut -d\".\" -f2- | rev`"
	cd "$zip_dirname"
	rm bin/*.bat
	rm bin/osc*.exe
	rm bin/audio_post_send.exe
	mkdir "$classes"/resources/win
	cp bin/* "$classes"/resources/win
	cp -r doc "$classes"/resources
	cp *.txt "$classes"/resources

	rm -rf audio_rxtx_*.zip

###################
	mkdir "$classes"/resources/mac

###################
	mkdir "$classes"/resources/lin32

###################
	mkdir "$classes"/resources/lin64

	echo "Main-Class: ch.lowres.audio_rxtx.gui.Main" > "$build"/Manifest.txt
	echo "" >> "$build"/Manifest.txt

	cd "$classes"

	now=`date +"%s"`

	echo "creating jar..."

	jar cfvm audio_rxtx_gui_"$now".jar "$build"/Manifest.txt ch/ com/ org/ resources/
	ls -l audio_rxtx_gui_"$now".jar
	echo "move audio_rxtx_gui_$now.jar to build dir..."
	mv audio_rxtx_gui_"$now".jar "$build"

	echo "start with"
	echo "java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -jar build/audio_rxtx_gui_$now.jar"

#osx:
#-Xdock:name="audio_rxtx GUI"

	#start now
	cd "$cur"
	java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -jar build/audio_rxtx_gui_$now.jar

	echo "build_jar done."
}

#========================================================================
function build_javadoc
{
	mkdir -p "$doc"
	javadoc -private -linksource -sourcetab 2 -d "$doc" \
	-classpath "$classes" \
	-sourcepath "$src" \
		ch.lowres.audio_rxtx.gui \
		ch.lowres.audio_rxtx.gui.widgets \
		ch.lowres.audio_rxtx.gui.helpers \
		ch.lowres.audio_rxtx.gui.api \
		ch.lowres.audio_rxtx.gui.osc
}

#execute:

mkdir -p "$build"
rm -rf "$build"/*

compile_java_osc
compile_gettext

create_build_info > "$src"/$package_path/helpers/BuildInfo.java
cat "$src"/$package_path/helpers/BuildInfo.java

create_languages
compile_audio_rxtx
#build_javadoc
build_jar
