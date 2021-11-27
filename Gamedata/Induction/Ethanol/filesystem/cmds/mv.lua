local usage_message = "usage: mv [options] [SOURCE] [DESTINATION]"

if tableLength(args) < 2 then
    print(usage_message)
    return
end

local originalPath = lunan.getSelectedPath() .. args[1]
local newPath = lunan.getSelectedPath() .. args[2]

-- will return nill if it doesn't exist, which evaluates to false in ifcat
local folderExists = lunan.getFolderChildren(newPath, false)

if not (folderExists == nil) then
    if not lunan.moveChild(originalPath, newPath) then
        print("no such file or directory")
    end
else
    lunan.renameChild(originalPath, args[2])
end
